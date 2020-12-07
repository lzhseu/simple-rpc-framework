package top.lzhseu.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.RpcRequestTransport;
import top.lzhseu.remoting.transport.netty.codec.NettyKryoDecoder;
import top.lzhseu.remoting.transport.netty.codec.NettyKryoEncoder;
import top.lzhseu.remoting.transport.netty.handler.NettyClientHandler;
import top.lzhseu.serialize.Serializer;
import top.lzhseu.serialize.kryo.KryoSerializer;

/**
 * @author lzh
 * @date 2020/12/4 15:30
 */
public class NettyClient implements RpcRequestTransport {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    static {
        Serializer serializer = new KryoSerializer();
        EventLoopGroup group = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(group).channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ch.pipeline().addLast(new NettyKryoDecoder(serializer, RpcResponse.class));
                        ch.pipeline().addLast(new NettyKryoEncoder(serializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });

    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        ChannelFuture channelFuture = null;
        try {
            channelFuture = b.connect(host, port).sync();
            logger.info("client connect {}", host + ":" + port);
            Channel channel = channelFuture.channel();
            logger.info("send message");
            if (channel != null) {
                channel.writeAndFlush(rpcRequest).addListener(future -> {
                    if (future.isSuccess()) {
                        logger.info("client send message: [{}]", rpcRequest.toString());
                    } else {
                        logger.error("Send failed:", future.cause());
                    }
                });

                // 阻塞等待，直到 Channel 关闭
                channel.closeFuture().sync();
                // 将服务端返回的数据 RpcResponse 对象取出
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return channel.attr(key).get();
            }

        } catch (InterruptedException e) {
            logger.error("occur exception when connect server:", e);
        }

        return null;
    }

    public static void main(String[] args) {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("interface")
                .methodName("hello").build();
        NettyClient nettyClient = new NettyClient("127.0.0.1", 9999);
        for (int i = 0; i < 3; i++) {
            nettyClient.sendRpcRequest(rpcRequest);
        }
        RpcResponse rpcResponse = (RpcResponse) nettyClient.sendRpcRequest(rpcRequest);
        System.out.println(rpcResponse.toString());
    }
}
