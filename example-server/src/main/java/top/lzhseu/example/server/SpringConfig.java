package top.lzhseu.example.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.lzhseu.remoting.transport.netty.server.NettyRpcServer;

/**
 * @author lzh
 * @date 2020/12/10 10:59
 */
@Configuration
public class SpringConfig {

    @Bean
    public NettyRpcServer nettyRpcServer() {
        return new NettyRpcServer();
    }
}
