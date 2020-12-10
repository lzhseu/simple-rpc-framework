package top.lzhseu.example.server;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.lzhseu.annotation.RpcScan;
import top.lzhseu.remoting.transport.netty.server.NettyRpcServer;

/**
 * @author lzh
 * @date 2020/12/9 21:33
 */
@RpcScan(basePackages = "top.lzhseu.example.server.serviceImpl")
public class RpcServer {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(RpcServer.class, SpringConfig.class);
        NettyRpcServer nettyRpcServer = (NettyRpcServer) context.getBean("nettyRpcServer");
        nettyRpcServer.start();
    }
}
