package top.lzhseu.config;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.registry.zk.utils.CuratorUtil;
import top.lzhseu.remoting.transport.netty.server.NettyRpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author lzh
 * @date 2020/12/7 19:50
 */
@Slf4j
public class ServerShutdownHook {

    public static void clearAll() {
        log.info("config server shutdown hook");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("invoke shutdown hook...");
                // 清理注册中心的服务
                InetSocketAddress address = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT);
                CuratorUtil.clearRegistry(CuratorUtil.getZkClient(), address);

                log.info("THE END");
            } catch (UnknownHostException ignored) {
            }
        }));
    }
}
