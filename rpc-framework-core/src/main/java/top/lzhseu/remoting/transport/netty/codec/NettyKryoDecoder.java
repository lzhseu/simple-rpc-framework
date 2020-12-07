package top.lzhseu.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.serialize.Serializer;

import java.util.List;

/**
 * 自定义解码器：用于反序列化
 * @author lzh
 * @date 2020/12/4 15:41
 */
@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;

    private final Class<?> genericClass;

    /**
     * Netty 传输的消息长度也就是对象序列化后对应的字节数组的大小，存储在 ByteBuf 头部
     */
    private static final int BODY_LENGTH = 4;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if (in.readableBytes() >= BODY_LENGTH) {

            in.markReaderIndex();

            int dataLength = in.readInt();

            if (dataLength < 0 || in.readableBytes() < 0) {
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }

            // 消息不完整
            if (in.readableBytes() != dataLength) {
                in.resetReaderIndex();
                return;
            }

            byte[] body = new byte[dataLength];
            in.readBytes(body);
            Object obj = serializer.deserialize(body, genericClass);
            out.add(obj);
            log.info("successfully decode ByteBuf to Object");
        }
    }
}
