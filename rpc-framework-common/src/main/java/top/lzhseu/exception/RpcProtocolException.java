package top.lzhseu.exception;

import top.lzhseu.enums.RpcErrorEnum;

/**
 * @author lzh
 * @date 2020/12/6 16:25
 */
public class RpcProtocolException extends RpcException {

    public RpcProtocolException() {
        super();
    }

    public RpcProtocolException(RpcErrorEnum rpcErrorEnum) {
        super(rpcErrorEnum);
    }

    public RpcProtocolException(RpcErrorEnum rpcErrorEnum, String detail) {
        super(rpcErrorEnum, detail);
    }

    public RpcProtocolException(RpcErrorEnum rpcErrorEnum, Throwable cause) {
        super(rpcErrorEnum, cause);
    }

    public RpcProtocolException(String message) {
        super(message);
    }


    public RpcProtocolException(String message, Throwable cause) {
        super(message, cause);
    }


}
