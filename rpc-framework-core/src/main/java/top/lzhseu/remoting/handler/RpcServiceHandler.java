package top.lzhseu.remoting.handler;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.enums.RpcErrorEnum;
import top.lzhseu.exception.RpcServiceException;
import top.lzhseu.provider.ServiceProvider;
import top.lzhseu.provider.impl.ServiceProviderImpl;
import top.lzhseu.remoting.dto.RpcRequest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RPC 服务处理端
 * <p>
 * Server stub 接收到 RPC 请求后，需要调用此类真正提供服务。
 * 此类会寻找指定的方法，执行调用后返回结果。
 * </p>
 * <p>此类应当实现为单例模式</p>
 *
 * @author lzh
 * @date 2020/12/8 14:03
 */
@Slf4j
public class RpcServiceHandler {

    private final ServiceProvider serviceProvider;

    private RpcServiceHandler() {
        serviceProvider = ServiceProviderImpl.getInstance();
    }

    public static RpcServiceHandler getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final RpcServiceHandler INSTANCE = new RpcServiceHandler();
    }

    /**
     *
     * @param rpcRequest RPC请求
     * @return 调用结果
     */
    public Object handle(RpcRequest rpcRequest) {
        // 拿到服务对象，如果未注册，会抛出异常
        Object service = serviceProvider.getService(rpcRequest.toRpcServiceProperties());
        return invoke(rpcRequest, service);
    }

    /**
     * 调用目标服务的方法
     *
     * @param rpcRequest RPC请求
     * @param service 服务对象
     * @return 调用结果
     */
    private Object invoke(RpcRequest rpcRequest, Object service) {
        try {

            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object result = method.invoke(service, rpcRequest.getParameters());
            log.info("service: [{}] successfully invoke method: [{}]", rpcRequest.toRpcServiceProperties().toRpcServiceName(), rpcRequest.getMethodName());
            return result;

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new RpcServiceException(RpcErrorEnum.SERVICE_METHOD_INVOKE_EXCEPTION, e);
        }
    }

}
