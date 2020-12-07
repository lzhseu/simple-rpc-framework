package top.lzhseu.remoting.transport.netty.client;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端和服务端连接后是不会主动断开的，除非网络故障等异常情况。
 * 所以，需要存放客户端与服务端连接的 channel。本类提供此功能。
 * 使用单例模式
 *
 * @author lzh
 * @date 2020/12/6 17:44
 */
@Slf4j
public class ChannelProvider {

    private final Map<String, Channel> channelMap;

    private ChannelProvider() {
        channelMap = new ConcurrentHashMap<>();
    }

    public static ChannelProvider getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ChannelProvider INSTANCE = new ChannelProvider();
    }


    public Channel get(InetSocketAddress address) {
        String key = address.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) {
                return channel;
            }
            channelMap.remove(key);
        }
        return null;
    }

    public void set(InetSocketAddress address, Channel channel) {
        String key = address.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress address) {
        String key = address.toString();
        channelMap.remove(key);
        log.info("after remove, size = [{}]", channelMap.size());
    }


}
