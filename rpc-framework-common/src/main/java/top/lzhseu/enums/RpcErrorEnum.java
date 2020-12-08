package top.lzhseu.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author lzh
 * @date 2020/12/7 16:59
 */
@AllArgsConstructor
@Getter
public enum RpcErrorEnum {

    /**
     * RPC 错误消息
     * 传输协议错误码：3xx
     * 服务注册/查询错误码：4xx
     */
    UNKNOWN_MAGIC_NUMBER_EXCEPTION(301, "魔数不匹配"),
    PROTOCOL_VERSION_ERROR_EXCEPTION(302, "RPC 传输协议版本不匹配"),
    SERVICE_NOT_FOUND_EXCEPTION(401, "未找到指定服务"),
    SERVICE_METHOD_INVOKE_EXCEPTION(402, "服务方法调用失败")
    ;

    private final int code;
    private final String message;
}
