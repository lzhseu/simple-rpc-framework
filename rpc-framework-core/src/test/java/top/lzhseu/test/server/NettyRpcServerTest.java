package top.lzhseu.test.server;

import top.lzhseu.entity.RpcServiceProperties;
import top.lzhseu.remoting.transport.netty.server.NettyRpcServer;
import top.lzhseu.test.server.serviceImpl.HelloServiceImpl;
import top.lzhseu.test.service.HelloService;

/**
 * @author lzh
 * @date 2020/12/8 14:38
 */
public class NettyRpcServerTest {

    public static void main(String[] args) {
        NettyRpcServer server = new NettyRpcServer();

        // 注册服务
        HelloService helloService1 = new HelloServiceImpl();
        server.registerService(helloService1,
                RpcServiceProperties.builder().group("test1").version("v_1.0").build());

        // 开启服务器
        server.start();
    }
}
