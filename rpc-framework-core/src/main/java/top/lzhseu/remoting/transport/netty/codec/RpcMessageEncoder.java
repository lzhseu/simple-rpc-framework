package top.lzhseu.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.compress.Compress;
import top.lzhseu.enums.CompressTypeEnum;
import top.lzhseu.enums.SerializationTypeEnum;
import top.lzhseu.extension.ExtensionLoader;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;
import top.lzhseu.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * RPC 协议编码器
 * RPC 协议具体内容见 {@link RpcMessage}
 *
 * @author lzh
 * @date 2020/12/5 20:29
 */
@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {

    /**
     * 其实这里也可以用普通的 int，因为每个 Channel 都会关联一个 ChannelPipeline
     * 每个 pipeline 里都会有若干 handler，这里的 handler 没有声明为 @Sharable
     * 而每次连接一个 Channel 也都会 new 一个新的 handler，
     * 所以对于一个 Channel 来说是线程安全的
     */
    private static final AtomicInteger REQUEST_ID = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) throws Exception {

        int fullLength = RpcConstant.RPC_MESSAGE_HEADER_LENGTH;

        try {
            out.writeBytes(RpcConstant.MAGIC_NUMBER);
            out.writeByte(RpcConstant.VERSION);
            // fullLength 还未计算
            out.writerIndex(out.writerIndex() + 4);
            byte messageType = msg.getMessageType();
            out.writeByte(messageType);
            out.writeByte(msg.getCodec());
            out.writeByte(msg.getCompress());
            out.writeInt(REQUEST_ID.getAndIncrement());

            // 如果是心跳消息，就不费劲去序列化和压缩了，因为心跳消息太简单了
            if (messageType != RpcConstant.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstant.HEARTBEAT_RESPONSE_TYPE) {

                byte[] bodyBytes;

                String codecName = SerializationTypeEnum.getName(msg.getCodec());
                log.info("serialization code: [{}]", msg.getCodec());
                log.info("serialization method: [{}]", codecName);
                //TODO: 目前使用 SPI 来做，后续可以尝试其他方案，如 反射＋抽象工厂模式 or Spring 配置文件来做
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                bodyBytes = serializer.serialize(msg.getData());

                // 压缩
                String compressName = CompressTypeEnum.getName(msg.getCompress());
                //TODO: 目前使用 SPI 来做，后续可以尝试其他方案，如 反射＋抽象工厂模式 or Spring 配置文件来做
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);

                fullLength += bodyBytes.length;
                out.writeBytes(bodyBytes);
            }

        } catch (Exception e) {
            log.error("Encode frame error", e);
        } finally {

            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex - fullLength + RpcConstant.MAGIC_NUMBER.length + 1);
            out.writeInt(fullLength);
            out.writerIndex(writeIndex);

        }

    }
}
