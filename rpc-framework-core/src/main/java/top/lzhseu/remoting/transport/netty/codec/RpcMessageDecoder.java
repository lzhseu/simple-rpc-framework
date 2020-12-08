package top.lzhseu.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;
import top.lzhseu.compress.Compress;
import top.lzhseu.enums.CompressTypeEnum;
import top.lzhseu.enums.RpcErrorEnum;
import top.lzhseu.enums.SerializationTypeEnum;
import top.lzhseu.exception.RpcProtocolException;
import top.lzhseu.extension.ExtensionLoader;
import top.lzhseu.remoting.constant.RpcConstant;
import top.lzhseu.remoting.dto.RpcMessage;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.serialize.Serializer;

import java.util.Arrays;

/**
 * RPC 协议解码器
 * RPC 协议具体内容见 {@link top.lzhseu.remoting.dto.RpcMessage}
 *
 * @author lzh
 * @date 2020/12/5 21:13
 */
@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        this(RpcConstant.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     *
     * @param maxFrameLength 帧的最大长度。 如果帧的长度大于此值，则将抛出TooLongFrameException 。
     * @param lengthFieldOffset 长度字段的偏移量
     * @param lengthFieldLength 长度字段的长度
     * @param lengthAdjustment 要添加到长度字段值的补偿值
     * @param initialBytesToStrip 要从解码帧中跳过字节数
     *                            如果需要接收所有标头+主体数据，则此值为 0
     *                            如果只想接收主体数据，则需要跳过标头消耗的字节数。
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset,
                             int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 直接拿到按长度解码得到的内容
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstant.RPC_MESSAGE_HEADER_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("Decode frame error.");
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf frame) {

        // 校对魔数
        byte[] magicBytes = new byte[RpcConstant.MAGIC_NUMBER.length];
        frame.readBytes(magicBytes);
        if (!Arrays.equals(magicBytes, RpcConstant.MAGIC_NUMBER)) {
            throw new RpcProtocolException(RpcErrorEnum.UNKNOWN_MAGIC_NUMBER_EXCEPTION, Arrays.toString(magicBytes));
        }

        // 校对版本号
        byte version = frame.readByte();
        if (version != RpcConstant.VERSION) {
            throw new RpcProtocolException(RpcErrorEnum.PROTOCOL_VERSION_ERROR_EXCEPTION, String.valueOf(version));
        }

        // 根据协议解码
        int fullLength = frame.readInt();
        byte messageType = frame.readByte();
        byte codecType = frame.readByte();
        byte compressType = frame.readByte();
        int requestId = frame.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .messageType(messageType)
                .codec(codecType)
                .compress(compressType)
                .requestId(requestId).build();

        if (messageType == RpcConstant.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstant.PING);
        } else if (messageType == RpcConstant.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstant.PONG);
        } else {

            int bodyLength = fullLength - RpcConstant.RPC_MESSAGE_HEADER_LENGTH;
            if (bodyLength > 0) {
                byte[] bodyBytes = new byte[bodyLength];
                frame.readBytes(bodyBytes);

                // 解压缩
                String compressName = CompressTypeEnum.getName(compressType);
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.decompress(bodyBytes);

                // 反序列化
                String codecName = SerializationTypeEnum.getName(codecType);
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);
                if (messageType == RpcConstant.RPC_REQUEST_TYPE) {
                    RpcRequest rpcRequest = serializer.deserialize(bodyBytes, RpcRequest.class);
                    rpcMessage.setData(rpcRequest);
                } else if (messageType == RpcConstant.RPC_RESPONSE_TYPE) {
                    RpcResponse rpcResponse = serializer.deserialize(bodyBytes, RpcResponse.class);
                    rpcMessage.setData(rpcResponse);
                }
            }
        }

        return rpcMessage;
    }


}
