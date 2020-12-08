package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/7 13:42
 */
@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * RPC 配置项
     * RPC_CONFIG_PATH: rpc 配置文件名
     * RPC_ZOOKEEPER_ADDRESS: Zookeeper 地址
     */
    RPC_CONFIG_PATH("rpc.properties"),
    RPC_ZOOKEEPER_ADDRESS("rpc.zookeeper.address");

    private final String value;

}
