package top.lzhseu.registry.zk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import top.lzhseu.enums.RpcConfigEnum;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.utils.PropertiesFileUtil;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Zookeeper 客户端工具类
 *
 * @author lzh
 * @date 2020/12/5 12:27
 */
@Slf4j
public final class CuratorUtil {

    // TODO: zookeeper 的属性可以通过配置文件配置

    /**
     * Zookeeper RPC 的根节点
     */
    public static final String ZK_REGISTER_ROOT_PATH = "/simple-rpc";

    /**
     * 重试之间等待的初始时间
     */
    private static final int BASE_SLEEP_TIME = 1000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 3;

    /**
     * 默认 Zookeeper 的地址
     */
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "127.0.0.1:2181";

    /**
     * 客户端
     */
    private static CuratorFramework zkClient;

    /**
     * 存储已经注册的路径节点
     */
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    /**
     * 缓存服务列表
     */
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    /**
     * 此类不允许被实例化
     */
    private CuratorUtil() { }

    /**
     * 创建持久性节点
     *
     * @param zkClient zk 客户端
     * @param path 节点路径
     */
    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                log.info("[zookeeper:] node [{}] has existed.", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                log.info("[zookeeper:] create node [{}] successfully", path);
            }

            REGISTERED_PATH_SET.add(path);

        } catch (Exception e) {
            log.error("create persistent node for path [{}] failed", path);
        }
    }

    /**
     * 获取某个节点下的所有子节点
     *
     * @param zkClient zk 客户端
     * @param rpcServiceName 服务名称
     * @return 该服务名称下的所有子节点，即提供该服务的所有服务地址
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }

        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        List<String> result = null;
        try {
            if (zkClient.checkExists().forPath(servicePath) != null) {

                result = zkClient.getChildren().forPath(servicePath);
                SERVICE_ADDRESS_MAP.put(rpcServiceName, result);


            } else {
                log.info("[zookeeper:] path [{}] not existed", servicePath);
            }
        } catch (Exception e) {
            log.error("[zookeeper:] get children nodes for path [{}] failed", servicePath);
        }

        return result;
    }

    /**
     * 获得客户端
     *
     * @return zookeeper client
     */
    public static CuratorFramework getZkClient() {

        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }

        Properties properties = PropertiesFileUtil.getPropertiesUnderClassPath(RpcConfigEnum.RPC_CONFIG_PATH.getValue());
        String zookeeperAddress = "";
        if (properties != null) {
            zookeeperAddress = (String) properties.getOrDefault(RpcConfigEnum.RPC_ZOOKEEPER_ADDRESS.getValue() ,DEFAULT_ZOOKEEPER_ADDRESS);
        }

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString(zookeeperAddress).build();
        zkClient.start();
        return zkClient;
    }


    /**
     * 清除下线服务提供者所注册的服务
     *
     * @param zkClient Zookeeper 客户端
     * @param inetSocketAddress 服务提供者地址
     */
    public static void clearRegistry(CuratorFramework zkClient, InetSocketAddress inetSocketAddress) {
        for (String path : REGISTERED_PATH_SET) {
            try {
                if (path.endsWith(inetSocketAddress.toString())) {
                    zkClient.delete().forPath(path);
                }
            } catch (Exception e) {
                log.error("[zookeeper:] clear registry for path [{}] failed", path);
            }
        }

        log.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET.toString());
    }

    /**
     * 注册监视器，监视节点的变化
     *
     * @param zkClient zk 客户端
     * @param rpcServiceName 服务名称
     */
    private static void registerWatcher(CuratorFramework zkClient, String rpcServiceName) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache cache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener listener = (curatorFramework, pathChildrenCacheEvent) -> {
            // 节点变化了，就重新获取节点下的地址
            List<String> serviceAddress = curatorFramework.getChildren().forPath(servicePath);
            log.info("[zookeeper:] node [{}] changed. then the service address is: {}", servicePath, serviceAddress);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddress);
        };

        cache.getListenable().addListener(listener);
        cache.start();

    }
}
