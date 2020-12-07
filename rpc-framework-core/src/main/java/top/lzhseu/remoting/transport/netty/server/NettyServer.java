package top.lzhseu.remoting.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.netty.codec.NettyKryoDecoder;
import top.lzhseu.remoting.transport.netty.codec.NettyKryoEncoder;
import top.lzhseu.remoting.transport.netty.handler.NettyServerHandler;
import top.lzhseu.serialize.Serializer;
import top.lzhseu.serialize.kryo.KryoSerializer;

/**
 * @author lzh
 * @date 2020/12/4 16:14
 */
@Slf4j
public class NettyServer {
    private final int port;

    public NettyServer(int port) {
        this.port = port;
    }

    private void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Serializer serializer = new KryoSerializer();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 启动 TCP_NODELAY，就意味着禁用了 Nagle 算法，允许小包的发送。
                    // 对于延时敏感型，同时数据传输量比较小的应用，开启 TCP_NODELAY 选项无疑是一个正确的选择。
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 是否开启 TCP 底层心跳机制
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 表示系统用于临时存放已完成三次握手的请求的队列的最大长度，
                    // 如果连接建立频繁，服务器处理创建新连接较慢，可以适当调大这个参数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new NettyKryoDecoder(serializer, RpcRequest.class));
                            ch.pipeline().addLast(new NettyKryoEncoder(serializer, RpcResponse.class));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            log.error("occur exception when start server:", e);
        } finally {
            log.error("shutdown bossGroup and workerGroup");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new NettyServer(9999).run();
    }
}
