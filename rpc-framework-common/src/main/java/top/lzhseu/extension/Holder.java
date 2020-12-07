package top.lzhseu.extension;

/**
 * @author lzh
 * @date 2020/12/6 11:46
 */
public class Holder<T> {

    private volatile T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
