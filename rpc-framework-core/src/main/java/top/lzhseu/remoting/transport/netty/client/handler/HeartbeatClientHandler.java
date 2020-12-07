package top.lzhseu.remoting.transport.netty.client.handler;

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
 * @date 2020/12/5 18:39
 */
@Slf4j
public class HeartbeatClientHandler extends SimpleChannelInboundHandler<RpcMessage> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcMessage msg) throws Exception {
        if (msg.getMessageType() == RpcConstant.HEARTBEAT_RESPONSE_TYPE) {

            log.info("heart beat [{}]", msg.getData());

        } else {

            // 把消息传递给下一个 handler，要注意增加引用计数
            ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent) {

            // 如果是一个 IdleStateEvent 事件，则发送心跳消息

            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {

                log.info("write idle event happen [{}]", ctx.channel().remoteAddress());

                // TODO: 序列化、压缩方式等写死，之后考虑通过配置文件来配置
                RpcMessage message = RpcMessage.builder()
                        .messageType(RpcConstant.HEARTBEAT_REQUEST_TYPE)
                        .codec(SerializationTypeEnum.KRYO.getCode())
                        .compress(CompressTypeEnum.GZIP.getCode())
                        .data(RpcConstant.PING).build();

                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }

        } else {
            // 如果不是一个 IdleStateEvent 事件，则传递给下一个 handler
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception when handle heart beat msg: ", cause);
        ctx.close();
    }
}
