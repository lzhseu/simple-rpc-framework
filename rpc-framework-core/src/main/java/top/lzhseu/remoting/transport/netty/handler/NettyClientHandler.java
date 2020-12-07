package top.lzhseu.remoting.transport.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.remoting.dto.RpcResponse;

/**
 * 自定义 ChannelHandler 处理服务端消息
 * @author lzh
 * @date 2020/12/4 15:37
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {

        log.info("Client receive msg: [{}]", msg.toString());
        // 声明一个 AttributeKey 对象
        AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
        // 将服务端的返回结果保存到 AttributeMap 上，AttributeMap 可以看作是一个Channel的共享数据源
        // AttributeMap的key是AttributeKey，value是Attribute
        ctx.channel().attr(key).set(msg);
        ctx.channel().close();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client caught exception", cause);
        ctx.close();
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== channelRegistered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== channelUnregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== channelInactive");
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=== channelReadComplete");
    }
}
