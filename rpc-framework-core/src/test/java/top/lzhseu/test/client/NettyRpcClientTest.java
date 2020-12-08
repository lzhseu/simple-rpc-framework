package top.lzhseu.test.client;

import org.junit.Assert;
import org.junit.Assert.*;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.netty.client.NettyRpcClient;
import top.lzhseu.test.service.Hello;
import top.lzhseu.test.service.HelloService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author lzh
 * @date 2020/12/8 14:52
 */
public class NettyRpcClientTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        NettyRpcClient client = new NettyRpcClient();

        Hello hello = new Hello("lzh", "hello rpc");
        Object[] parameters = {hello};

        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId("001")
                .interfaceName(HelloService.class.getCanonicalName())
                .methodName("sayHello")
                .parameters(parameters)
                .parameterTypes(new Class[]{Hello.class})
                .group("test1")
                .version("v_1.0").build();
        CompletableFuture<RpcResponse<Object>> result = (CompletableFuture<RpcResponse<Object>>) client.sendRpcRequest(rpcRequest);
        System.out.println("result: " + result.get().getData());



    }
}
