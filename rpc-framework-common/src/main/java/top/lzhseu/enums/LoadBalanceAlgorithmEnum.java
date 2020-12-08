package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/7 19:36
 */
@AllArgsConstructor
@Getter
public enum LoadBalanceAlgorithmEnum {

    /**
     * 负载均衡算法：
     * 加权随机
     * 一致性哈希
     * 最小活跃数
     * 加权轮询
     */
    RANDOM("random"),
    CONSISTENT_HASH("consistent-hash"),
    LEAST_ACTIVE("least-active"),
    ROUND_ROBIN("round-robin");

    private final String name;
}
