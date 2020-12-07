package top.lzhseu.registry.zk.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFrameworkFactory.Builder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * Zookeeper 客户端工具类
 *
 * @author lzh
 * @date 2020/12/5 12:27
 */
@Slf4j
public final class CuratorUtil {

    /**
     * 重试之间等待的初始时间
     */
    private static final int BASE_SLEEP_TIME = 1000;

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRIES = 3;

    /**
     * 此类不允许被实例化
     */
    private CuratorUtil() {

    }

    public static void createPersistentNode(CuratorFramework zkClient, String path) {
        try {
            zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
        } catch (Exception e) {
            log.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * 获得客户端
     * @return zookeeper client
     */
    public static CuratorFramework getZkClient() {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        CuratorFramework zkClient = CuratorFrameworkFactory.builder()
                .retryPolicy(retryPolicy)
                .connectString("47.111.241.176:2181").build();
        zkClient.start();
        return zkClient;
    }
}
