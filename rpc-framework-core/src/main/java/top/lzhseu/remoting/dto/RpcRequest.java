package top.lzhseu.remoting.dto;

import lombok.*;

import java.io.Serializable;

/**
 * RPC 请求的网络传输实体
 *
 * @author lzh
 * @date 2020/12/4 15:13
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@ToString
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -6993182028426205064L;

    /**
     * 请求 id
     */
    private String requestId;

    /**
     * 接口名称
     */
    private String interfaceName;

    /**
     * 方法名称
     */
    private String methodName;

    /**
     * 方法参数
     */
    private Object[] parameters;

    /**
     * 方法参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 服务版本号。为后续不兼容升级提供可能
     */
    private String version;

    /**
     * 用于处理一个接口有多个实现类的情况
     * 不同实现类有一个唯一标识
     */
    private String group;
}
