package top.lzhseu.remoting.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.remoting.transport.netty.codec.RpcMessageCodec;
import top.lzhseu.remoting.transport.netty.server.handler.HeartBearServerHandler;
import top.lzhseu.remoting.transport.netty.server.handler.NettyRpcServerHandler;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 * @date 2020/12/6 20:30
 */
@Slf4j
public class NettyRpcServer {

    /**
     * TODO: 端口应该可配置
     */
    public static final int port = 9999;

    @SneakyThrows
    public void start() {

        // TODO: 配置钩子，当服务器关闭获取 JVM 退出时

        String host = InetAddress.getLocalHost().getHostAddress();
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup= new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 启动 TCP_NODELAY，就意味着禁用了 Nagle 算法，允许小包的发送。
                    // 对于延时敏感型，同时数据传输量比较小的应用，开启 TCP_NODELAY 选项无疑是一个正确的选择。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制，我们在应用层已经有实现心跳机制了，这里不配置完全没问题
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，
                    // 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            // 30 秒之内没有收到客户端请求的话就关闭连接
                            ch.pipeline().addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                            ch.pipeline().addLast(new RpcMessageCodec());
                            ch.pipeline().addLast(new HeartBearServerHandler());
                            ch.pipeline().addLast(new NettyRpcServerHandler());
                        }
                    });

            ChannelFuture f = bootstrap.bind(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyRpcServer().start();
    }
}
