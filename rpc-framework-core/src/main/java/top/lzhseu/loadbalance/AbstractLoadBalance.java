package top.lzhseu.loadbalance;

import java.util.List;

/**
 * http://dubbo.apache.org/zh/docs/v2.7/dev/source/loadbalance/#2源码分析
 *
 * @author lzh
 * @date 2020/12/7 17:33
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectRemoteServiceAddress(List<String> serviceAddress, String rpcServiceName) {
        if (serviceAddress == null || serviceAddress.isEmpty()) {
            return null;
        }

        if (serviceAddress.size() == 1) {
            return serviceAddress.get(0);
        }

        return doSelect(serviceAddress, rpcServiceName);
    }

    protected abstract String doSelect(List<String> serviceAddress, String rpcServiceName);
}
