package top.lzhseu.registry;

import top.lzhseu.extension.SPI;

import java.net.InetSocketAddress;

/**
 * 注册服务
 *
 * @author lzh
 * @date 2020/12/7 11:48
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务
     *
     * @param rpcServiceName 完整服务名称（interface name + version + group）
     * @param address 服务提供者的地址(ip : PORT)
     */
    void registerService(String rpcServiceName, InetSocketAddress address);
}
