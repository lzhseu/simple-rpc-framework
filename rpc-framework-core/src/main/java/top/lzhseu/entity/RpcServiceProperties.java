package top.lzhseu.entity;

import lombok.*;

/**
 * RPC 服务的实体
 * @author lzh
 * @date 2020/12/7 20:52
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RpcServiceProperties {

    public static final String DEFAULT_VERSION = "";
    public static final String DEFAULT_GROUP = "";

    /**
     * 服务版本号
     */
    private String version;

    /**
     * 如果一个接口有多个实现类，用 group 区分
     */
    private String group;

    /**
     * 服务，即接口名
     */
    private String serviceName;

    /**
     * 生成 RPC 服务名称
     * @return RPC 服务名称
     */
    public String toRpcServiceName() {
        return serviceName + "#" + group + "#" + version;
    }

}
