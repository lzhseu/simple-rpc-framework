package top.lzhseu.exception;

/**
 * @author lzh
 * @date 2020/12/4 16:49
 */
public class SerializeException extends RuntimeException {

    public SerializeException() {
        super();
    }


    public SerializeException(String message) {
        super(message);
    }


    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }


    public SerializeException(Throwable cause) {
        super(cause);
    }
}
