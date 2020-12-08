package top.lzhseu.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import top.lzhseu.registry.ServiceRegistry;
import top.lzhseu.registry.zk.utils.CuratorUtil;

import java.net.InetSocketAddress;

/**
 * Zookeeper 实现注册中心，注册服务
 *
 * @author lzh
 * @date 2020/12/7 11:55
 */
public class ZkServiceRegistry implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        // address 的形式为 /192.168.177.50:9090，所以不用再加一个 /
        String servicePath = CuratorUtil.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + address;
        CuratorFramework zkClient = CuratorUtil.getZkClient();
        CuratorUtil.createPersistentNode(zkClient, servicePath);
    }
}
