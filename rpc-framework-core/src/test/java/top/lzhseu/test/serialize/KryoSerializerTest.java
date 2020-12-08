package top.lzhseu.test.serialize;

import org.junit.jupiter.api.Test;
import top.lzhseu.registry.zk.ZkServiceDiscovery;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.serialize.kryo.KryoSerializer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * @author lzh
 * @date 2020/12/4 17:05
 */
public class KryoSerializerTest {

    private KryoSerializer kryoSerializer = new KryoSerializer();

    @Test
    public void testSerialize() {
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName("top.lzhseu.Interface1")
                .methodName("method1").build();
        byte[] bytes = kryoSerializer.serialize(rpcRequest);
        System.out.println("==序列化==");
        System.out.println(bytes.length);
        RpcRequest newRpcRequest = kryoSerializer.deserialize(bytes, RpcRequest.class);
        System.out.println("==反序列化==");
        System.out.println(newRpcRequest);
    }

    @Test
    public void testSerializeString() {
        String str = "ping";
        byte[] bytes = kryoSerializer.serialize(str);
        System.out.println("序列化后的长度：" + bytes.length);
        for (byte b : bytes) {
            System.out.print(b + " ");
        }
        System.out.println();
        String newStr = kryoSerializer.deserialize(bytes, String.class);
        System.out.println("反序列化后的结果："  + newStr);
    }

    @Test
    public void test() {
        System.out.println(ZkServiceDiscovery.class.getCanonicalName());
        System.out.println(ZkServiceDiscovery.class.getName());
        System.out.println(ZkServiceDiscovery.class.getSimpleName());
    }

}
