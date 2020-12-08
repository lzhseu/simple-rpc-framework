package top.lzhseu.proxy;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.entity.RpcServiceProperties;
import top.lzhseu.enums.RpcErrorEnum;
import top.lzhseu.enums.RpcResponseEnum;
import top.lzhseu.exception.RpcServiceException;
import top.lzhseu.remoting.dto.RpcRequest;
import top.lzhseu.remoting.dto.RpcResponse;
import top.lzhseu.remoting.transport.RpcRequestTransport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 动态代理 RPC 客户端，这样就可以屏蔽网络传输等细节。从而使得调用远程方法与调用本地方法没有区别。
 *
 * @author lzh
 * @date 2020/12/8 16:27
 */
@Slf4j
public class RpcClientProxy implements InvocationHandler {

    private final RpcRequestTransport rpcRequestTrans;

    private final RpcServiceProperties rpcServiceProperties;

    public RpcClientProxy(RpcRequestTransport rpcRequestTrans, RpcServiceProperties rpcServiceProperties) {

        this.rpcRequestTrans = rpcRequestTrans;

        if (rpcServiceProperties.getGroup() == null) {
            rpcServiceProperties.setGroup(RpcServiceProperties.DEFAULT_GROUP);
        }

        if (rpcServiceProperties.getVersion() == null) {
            rpcServiceProperties.setVersion(RpcServiceProperties.DEFAULT_VERSION);
        }

        this.rpcServiceProperties = rpcServiceProperties;
    }

    public RpcClientProxy(RpcRequestTransport rpcRequestTrans) {
        this.rpcRequestTrans = rpcRequestTrans;
        this.rpcServiceProperties = RpcServiceProperties.builder()
                .group(RpcServiceProperties.DEFAULT_GROUP)
                .version(RpcServiceProperties.DEFAULT_VERSION).build();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        log.info("invoke method: [{}]", method.getName());

        // 封装请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .requestId(UUID.randomUUID().toString())
                .group(rpcServiceProperties.getGroup())
                .version(rpcServiceProperties.getVersion()).build();

        // 使用实际的 Netty 客户端传送
        CompletableFuture<RpcResponse<Object>> future = (CompletableFuture<RpcResponse<Object>>) rpcRequestTrans.sendRpcRequest(rpcRequest);
        RpcResponse<Object> rpcResponse = future.get();

        check(rpcRequest, rpcResponse);

        return rpcResponse.getData();
    }

    private void check(RpcRequest rpcRequest, RpcResponse<Object> rpcResponse) {

        if (rpcResponse == null) {
            throw new RpcServiceException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE_EXCEPTION, rpcRequest.getInterfaceName());
        }

        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcServiceException(RpcErrorEnum.RESPONSE_NOT_MATCHED_REQUEST_EXCEPTION, rpcRequest.getInterfaceName());
        }

        if (rpcResponse.getCode() == null || !rpcResponse.getCode().equals(RpcResponseEnum.SUCCESS.getCode())) {
            throw new RpcServiceException(RpcErrorEnum.SERVICE_INVOCATION_FAILURE_EXCEPTION, rpcRequest.getInterfaceName());
        }

    }

    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }
}
