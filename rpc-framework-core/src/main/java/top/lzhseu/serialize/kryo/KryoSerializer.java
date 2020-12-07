package top.lzhseu.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 使用 Kryo https://github.com/EsotericSoftware/kryo 序列化类
 * @author lzh
 * @date 2020/12/4 16:22
 */
public class KryoSerializer implements Serializer {

    /**
     * Kryo 不是线程安全的。所以使用 ThreadLocal 存放 Kryo 对象
     * 这样，每个线程都有自己的 Kryo 实例
     */
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        // 默认值为 true，是否关闭注册行为，关闭之后可能存在序列化问题，一般推荐设置为 true
        kryo.setReferences(true);
        // 默认值为 false, 如果 true，遇到未注册的类会报异常
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {

            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();

            return output.toBytes();

        } catch (IOException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {

            Kryo kryo = kryoThreadLocal.get();
            Object object = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return clazz.cast(object);

        } catch (IOException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }
}
