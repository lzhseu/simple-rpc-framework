package top.lzhseu.remoting.dto;

import lombok.*;
import top.lzhseu.enums.RpcResponseEnum;

import java.io.Serializable;

/**
 * RPC 响应的网络传输实体
 * @author lzh
 * @date 2020/12/4 15:13
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 5697539544711535422L;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 响应码
     */
    private Integer code;

    /**
     * 响应信息，是响应码的解释
     */
    private String message;

    /**
     * 响应体，即具体内容
     */
    private T data;

    public static <S> RpcResponse<S> SUCCESS(String requestId, S data) {
        RpcResponse<S> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseEnum.SUCCESS.getCode());
        rpcResponse.setMessage(RpcResponseEnum.SUCCESS.getMessage());
        rpcResponse.setRequestId(requestId);
        if (data != null) {
            rpcResponse.setData(data);
        }
        return rpcResponse;
    }

    public static <S> RpcResponse<S> FAIL(RpcResponseEnum rpcResponseEnum) {
        RpcResponse<S> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(rpcResponseEnum.getCode());
        rpcResponse.setMessage(rpcResponseEnum.getMessage());
        return rpcResponse;
    }
}
