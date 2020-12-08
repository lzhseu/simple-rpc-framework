package top.lzhseu.test.registry.zk;

import org.apache.curator.framework.CuratorFramework;
import org.junit.jupiter.api.Test;
import top.lzhseu.registry.zk.ZkServiceDiscovery;
import top.lzhseu.registry.zk.ZkServiceRegistry;
import top.lzhseu.registry.zk.utils.CuratorUtil;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author lzh
 * @date 2020/12/7 14:56
 */
public class ZkServiceRegistryTest {

    private String serviceName = ZkServiceRegistry.class.getName();

    @Test
    public void testRegisterService() throws UnknownHostException {
        ZkServiceRegistry zkServiceRegistry = new ZkServiceRegistry();
        zkServiceRegistry.registerService(serviceName,
                new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 9090));
    }

    @Test
    public void testDiscoveryService() {
        ZkServiceDiscovery zkServiceDiscovery = new ZkServiceDiscovery();
        InetSocketAddress inetSocketAddress = zkServiceDiscovery.lookupService(serviceName);
        System.out.println(inetSocketAddress);
    }
}
