package top.lzhseu.remoting.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.CompressTypeEnum;
import top.lzhseu.enums.SerializationTypeEnum;
import top.lzhseu.extension.ExtensionLoader;
import top.lzhseu.registry.ServiceDiscovery;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.RpcRequestTransport;
import top.lzhseu.remoting.transport.netty.client.handler.HeartbeatClientHandler;
import top.lzhseu.remoting.transport.netty.client.handler.NettyRpcClientHandler;
import top.lzhseu.remoting.transport.netty.codec.RpcMessageCodec;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author lzh
 * @date 2020/12/5 19:42
 */
@Slf4j
public class NettyRpcClient implements RpcRequestTransport {

    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;
    private final ChannelProvider channelProvider;
    private final UnprocessedRequests unprocessedRequests;
    private final ServiceDiscovery serviceDiscovery;

    public NettyRpcClient() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接超时时间
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        // 如果 10s 内没有发送请求，则发送心跳包
                        ch.pipeline().addLast(new IdleStateHandler(0, 10, 0, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new RpcMessageCodec());
                        ch.pipeline().addLast(new HeartbeatClientHandler());
                        ch.pipeline().addLast(new NettyRpcClientHandler());
                    }
                });
        channelProvider = ChannelProvider.getInstance();
        unprocessedRequests = UnprocessedRequests.getInstance();
        serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class).getExtension("zk");
    }

    /**
     * 用于连接服务端（目标方法所在的服务器）并返回对应的 Channel
     * @param address 远程服务提供者的地址
     * @return channel
     */
    @SneakyThrows
    public Channel doConnect(InetSocketAddress address) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();

        bootstrap.connect(address).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("the client connected to [{}] successfully", address.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });

        return completableFuture.get();
    }

    /**
     * 根据远程地址获取连接的 channel
     * @param address 远程服务提供者的地址
     * @return channel
     */
    public Channel getChannel(InetSocketAddress address) {
        Channel channel = channelProvider.get(address);
        if (channel == null) {
            channel = doConnect(address);
            channelProvider.set(address, channel);
        }
        return channel;
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {

        // 返回值 Future
        CompletableFuture<RpcResponse<Object>> completableFuture = new CompletableFuture<>();

        // 找到远程服务的地址，发起请求
        InetSocketAddress remoteAddress = serviceDiscovery.lookupService(rpcRequest.toRpcServiceProperties().toRpcServiceName());

        // 拿到连接的 channel
        Channel channel = getChannel(remoteAddress);

        if (channel.isActive()) {
            // 封装请求
            // TODO: 序列化、压缩方式等写死，之后考虑通过配置文件来配置
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(RpcConstant.RPC_REQUEST_TYPE)
                    .codec(SerializationTypeEnum.KRYO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .data(rpcRequest).build();

            // 把未处理请求的未来返回值放入未处理队列，之后可从其他地方通过 requestId 获取结果
            unprocessedRequests.put(rpcRequest.getRequestId(), completableFuture);

            // 发送请求
            channel.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    completableFuture.completeExceptionally(future.cause());
                    log.error("client send message error: ", future.cause());
                }
            });
        } else {
            log.error("channel to [{}] is not active", channel.remoteAddress());
            throw new IllegalStateException();
        }

        return completableFuture;
    }

    public void close() {
        eventLoopGroup.shutdownGracefully();
    }

}
