package top.lzhseu.remoting.transport.netty.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.CompressTypeEnum;
import top.lzhseu.enums.RpcResponseEnum;
import top.lzhseu.enums.SerializationTypeEnum;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.handler.RpcServiceHandler;

/**
 * 服务端端 handler，用于处理从客户端接收到的数据
 *
 * @author lzh
 * @date 2020/12/6 20:31
 */
@Slf4j
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

    private final RpcServiceHandler rpcServiceHandler = RpcServiceHandler.getInstance();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {

        log.info("server receive message: [{}]", msg);

        byte messageType = msg.getMessageType();

        if (messageType == RpcConstant.RPC_REQUEST_TYPE) {

            // 拿出 RpcRequest
            RpcRequest rpcRequest = (RpcRequest) msg.getData();

            // 解析请求，找到目标服务，并得到执行结果
            Object result = rpcServiceHandler.handle(rpcRequest);
            log.info("server get result: {}", result.toString());

            // 封装消息，并发送
            // TODO: 序列化、压缩方式等写死，之后考虑通过配置文件来配置
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(RpcConstant.RPC_RESPONSE_TYPE)
                    .codec(SerializationTypeEnum.KRYO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .build();

            if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                rpcMessage.setData(RpcResponse.SUCCESS(rpcRequest.getRequestId(), result));
            } else {
                rpcMessage.setData(RpcResponse.FAIL(RpcResponseEnum.FAIL));
                log.error("not writable now, message dropped");
            }

            ctx.writeAndFlush(rpcMessage).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("server send msg: [{}]", rpcMessage);
                } else {
                    future.channel().close();
                    log.error("server send msg error!", future.cause());
                }
            });

        } else {
            log.error("message type [{}] not compatible", messageType);
        }


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception when handle request msg: [client id {}]", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
