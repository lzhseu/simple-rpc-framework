package top.lzhseu.test.client;

import org.junit.Assert;
import org.junit.Assert.*;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import top.lzhseu.annotation.RpcReference;
import top.lzhseu.entity.RpcServiceProperties;
import top.lzhseu.proxy.RpcClientProxy;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.RpcRequestTransport;
import top.lzhseu.remoting.transport.netty.client.NettyRpcClient;
import top.lzhseu.spring.SpringConfiguration;
import top.lzhseu.test.service.Hello;
import top.lzhseu.test.service.HelloService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author lzh
 * @date 2020/12/8 14:52
 */
public class NettyRpcClientTest {

    @Test
     public void testNettyRpcClient() throws ExecutionException, InterruptedException {

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

    @Test
    public void testNettyRpcClientProxy() {

        RpcRequestTransport requestTransport = new NettyRpcClient();
        RpcServiceProperties rpcServiceProperties = RpcServiceProperties.builder()
                .group("test1")
                .version("v_1.0").build();
        RpcClientProxy proxy = new RpcClientProxy(requestTransport, rpcServiceProperties);
        HelloService helloService = proxy.getProxy(HelloService.class);
        String s = helloService.sayHello(new Hello("lzh", "Good evening"));
        System.out.println(s);
    }

    @Test
    public void testNettyRpcClientWithAnno() {
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfiguration.class);
        HelloController helloController = (HelloController) context.getBean("helloController");
        helloController.test();
    }
}
