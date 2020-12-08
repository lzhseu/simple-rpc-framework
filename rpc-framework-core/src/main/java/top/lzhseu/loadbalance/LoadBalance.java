package top.lzhseu.loadbalance;

import top.lzhseu.extension.SPI;

import java.util.List;

/**
 * 负载均衡策略
 *
 * @author lzh
 * @date 2020/12/7 16:03
 */
@SPI
public interface LoadBalance {

    /**
     * 使用负载均衡算法取得一个服务地址
     *
     * @param serviceAddress 提供服务的服务地址列表
     * @param rpcServiceName 服务名称
     * @return 服务地址
     */
    String selectRemoteServiceAddress(List<String> serviceAddress, String rpcServiceName);
}
