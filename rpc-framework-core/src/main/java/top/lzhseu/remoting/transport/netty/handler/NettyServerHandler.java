package top.lzhseu.remoting.transport.netty.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义服务端处理器，用于处理客户端消息
 *
 * @author lzh
 * @date 2020/12/4 17:48
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    private static final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            RpcRequest rpcRequest = (RpcRequest) msg;
            log.info("server receive msg: [{}] ,times:[{}]", rpcRequest, atomicInteger.getAndIncrement());
            RpcResponse rpcResponse = RpcResponse.builder().message("message from server").build();
            ChannelFuture future = ctx.writeAndFlush(rpcResponse);
            future.addListener(ChannelFutureListener.CLOSE);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception",cause);
        ctx.close();
    }
}
