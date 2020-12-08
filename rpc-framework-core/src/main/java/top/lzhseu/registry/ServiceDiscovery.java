package top.lzhseu.registry;

import java.net.InetSocketAddress;

/**
 * 查询服务
 *
 * @author lzh
 * @date 2020/12/7 15:58
 */
public interface ServiceDiscovery {

    /**
     * 根据服务名称查询远程服务器的地址
     *
     * @param rpcServiceName 服务名称
     * @return 服务提供者的地址
     */
    InetSocketAddress lookupService(String rpcServiceName);
}
