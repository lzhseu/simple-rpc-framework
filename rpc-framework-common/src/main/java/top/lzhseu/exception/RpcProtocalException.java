package top.lzhseu.exception;

/**
 * @author lzh
 * @date 2020/12/6 16:25
 */
public class RpcProtocalException extends RuntimeException {

    public RpcProtocalException() {
        super();
    }


    public RpcProtocalException(String message) {
        super(message);
    }


    public RpcProtocalException(String message, Throwable cause) {
        super(message, cause);
    }


    public RpcProtocalException(Throwable cause) {
        super(cause);
    }
}
