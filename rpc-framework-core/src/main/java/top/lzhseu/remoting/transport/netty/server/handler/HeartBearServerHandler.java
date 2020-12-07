package top.lzhseu.remoting.transport.netty.server.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.CompressTypeEnum;
import top.lzhseu.enums.SerializationTypeEnum;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;

/**
 * 处理心跳信息的 handler
 *
 * @author lzh
 * @date 2020/12/6 20:30
 */
@Slf4j
public class HeartBearServerHandler extends SimpleChannelInboundHandler<RpcMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {

        if (msg.getMessageType() == RpcConstant.HEARTBEAT_REQUEST_TYPE) {

            // 如果是心跳请求，则发送心跳响应

            log.info("heart beat [{}]", msg.getData());

            // TODO: 序列化、压缩方式等写死，之后考虑通过配置文件来配置
            RpcMessage rpcMessage = RpcMessage.builder()
                    .messageType(RpcConstant.HEARTBEAT_RESPONSE_TYPE)
                    .codec(SerializationTypeEnum.KRYO.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .data(RpcConstant.PONG).build();

            ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        } else {
            ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {

            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                // 如果规定时间内没收到消息，则关闭该 channel
                log.info("write idle event happen, so close the connection: [{}]", ctx.channel().remoteAddress());
                ctx.close();
            }

        } else {
            // 如果不是一个 IdleStateEvent 事件，则传递给下一个 handler
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server catch exception when handle heart beat msg: [client id {}]", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }
}
