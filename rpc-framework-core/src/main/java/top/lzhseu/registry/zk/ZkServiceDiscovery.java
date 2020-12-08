package top.lzhseu.registry.zk;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.LoadBalanceAlgorithmEnum;
import top.lzhseu.enums.RpcErrorEnum;
import top.lzhseu.exception.RpcServiceException;
import top.lzhseu.extension.ExtensionLoader;
import top.lzhseu.loadbalance.LoadBalance;
import top.lzhseu.registry.ServiceDiscovery;
import top.lzhseu.registry.zk.utils.CuratorUtil;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author lzh
 * @date 2020/12/7 16:01
 */
@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {

    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                .getExtension(LoadBalanceAlgorithmEnum.CONSISTENT_HASH.getName());
    }

    @Override
    public InetSocketAddress lookupService(String rpcServiceName) {
        List<String> serviceAddresses = CuratorUtil.getChildrenNodes(CuratorUtil.getZkClient(), rpcServiceName);
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            throw new RpcServiceException(RpcErrorEnum.SERVICE_NOT_FOUND_EXCEPTION, rpcServiceName);
        }

        // 使用负载均衡算法找出一个地址
        String targetAddress = loadBalance.selectRemoteServiceAddress(serviceAddresses, rpcServiceName);
        log.info("Successfully found a service address [{}]", targetAddress);
        String[] socketArr = targetAddress.split(":");
        String host = socketArr[0];
        int port = Integer.parseInt(socketArr[1]);
        return new InetSocketAddress(host, port);
    }
}
