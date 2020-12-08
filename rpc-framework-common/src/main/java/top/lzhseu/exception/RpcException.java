package top.lzhseu.exception;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.RpcErrorEnum;

/**
 * @author lzh
 * @date 2020/12/7 16:54
 */
@Slf4j
public class RpcException extends RuntimeException {

    public RpcException() {
        super();
        log.error(this.getClass().getName(), this);
    }

    public RpcException(RpcErrorEnum rpcErrorEnum) {
        super(rpcErrorEnum.getMessage());
        log.error(rpcErrorEnum.getMessage(), this);
    }

    public RpcException(RpcErrorEnum rpcErrorEnum, String detail) {
        super(rpcErrorEnum.getMessage() + ":" + detail);
        log.error(rpcErrorEnum.getMessage() + ":" + detail, this);
    }

    public RpcException(RpcErrorEnum rpcErrorEnum, Throwable cause) {
        super(rpcErrorEnum.getMessage(), cause);
        log.error(rpcErrorEnum.getMessage(), this);
    }


    public RpcException(String message) {
        super(message);
        log.error(message, this);
    }


    public RpcException(String message, Throwable cause) {
        super(message, cause);
        log.error(message, this);
    }

}
