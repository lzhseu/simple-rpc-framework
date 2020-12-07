package top.lzhseu.remoting.transport.netty.codec;

import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * @author lzh
 * @date 2020/12/6 17:35
 */
public class RpcMessageCodec extends CombinedChannelDuplexHandler<RpcMessageDecoder, RpcMessageEncoder> {

    public RpcMessageCodec() {
        // 将委托实例传递给父类
        super(new RpcMessageDecoder(), new RpcMessageEncoder());
    }
}
