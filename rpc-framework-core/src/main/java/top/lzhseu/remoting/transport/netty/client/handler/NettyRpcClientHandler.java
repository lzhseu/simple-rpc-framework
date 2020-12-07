package top.lzhseu.remoting.transport.netty.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.netty.client.UnprocessedRequests;

/**
 * 客户端 handler，用于处理从服务端接收到的数据
 *
 * @author lzh
 * @date 2020/12/5 20:03
 */
@Slf4j
public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private final UnprocessedRequests unprocessedRequests;

    public NettyRpcClientHandler() {
        unprocessedRequests = UnprocessedRequests.getInstance();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        log.info("client receive msg: [{}]", msg);
        byte messageType = msg.getMessageType();
        if (messageType == RpcConstant.RPC_RESPONSE_TYPE) {
            RpcResponse<Object> rpcResponse = (RpcResponse<Object>) msg.getData();
            // 通知其他地方可以获取到结果了
            unprocessedRequests.complete(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception when handle response msg", cause);
        ctx.close();
    }

}
