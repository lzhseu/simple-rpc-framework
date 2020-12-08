package top.lzhseu.loadbalance.impl;

import top.lzhseu.loadbalance.AbstractLoadBalance;

import java.util.List;
import java.util.Random;

/**
 * http://dubbo.apache.org/zh/docs/v2.7/dev/source/loadbalance/#21-randomloadbalance
 * 简单的随机策略
 *
 * @author lzh
 * @date 2020/12/7 17:46
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    protected String doSelect(List<String> serviceAddress, String rpcServiceName) {
        return serviceAddress.get(random.nextInt(serviceAddress.size()));
    }
}
