package top.lzhseu.exception;

import top.lzhseu.enums.RpcErrorEnum;

/**
 * @author lzh
 * @date 2020/12/7 17:30
 */
public class RpcServiceException extends RpcException {

    public RpcServiceException() {
        super();
    }

    public RpcServiceException(RpcErrorEnum rpcErrorEnum) {
        super(rpcErrorEnum);
    }

    public RpcServiceException(RpcErrorEnum rpcErrorEnum, String detail) {
        super(rpcErrorEnum, detail);
    }

    public RpcServiceException(RpcErrorEnum rpcErrorEnum, Throwable cause) {
        super(rpcErrorEnum, cause);
    }


    public RpcServiceException(String message) {
        super(message);
    }


    public RpcServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
