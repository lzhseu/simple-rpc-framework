package top.lzhseu.provider.impl;

import lombok.extern.slf4j.Slf4j;
import top.lzhseu.entity.RpcServiceProperties;
import top.lzhseu.enums.RpcErrorEnum;
import top.lzhseu.exception.RpcServiceException;
import top.lzhseu.extension.ExtensionLoader;
import top.lzhseu.provider.ServiceProvider;
import top.lzhseu.registry.ServiceRegistry;
import top.lzhseu.remoting.transport.netty.server.NettyRpcServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现为单例模式
 *
 * @author lzh
 * @date 2020/12/8 9:58
 */
@Slf4j
public class ServiceProviderImpl implements ServiceProvider {

    /**
     * key 注册的服务名称（接口名+version+group）
     * value 服务对象
     */
    private final Map<String, Object> serviceMap;

    /**
     * 服务注册中心
     */
    private final ServiceRegistry serviceRegistry;

    private ServiceProviderImpl() {
        serviceMap = new ConcurrentHashMap<>();
        serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class).getExtension("zk");
    }

    public static ServiceProviderImpl getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static final ServiceProviderImpl INSTANCE = new ServiceProviderImpl();
    }

    @Override
    public void addService(Object service, Class<?> serviceClass, RpcServiceProperties properties) {
        properties.setServiceName(serviceClass.getCanonicalName());
        String rpcServiceName = properties.toRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceMap.put(rpcServiceName, service);
        log.info("Successfully add service {} and interface {}", rpcServiceName, serviceClass);
    }

    @Override
    public Object getService(RpcServiceProperties properties) {
        Object service = serviceMap.get(properties.toRpcServiceName());
        if (service == null) {
            throw new RpcServiceException(RpcErrorEnum.SERVICE_NOT_FOUND_EXCEPTION, properties.toRpcServiceName());
        }
        return service;
    }

    @Override
    public void publishService(Object service, Class<?> serviceClass, RpcServiceProperties properties) {

        try {
            addService(service, serviceClass, properties);
            serviceRegistry.registerService(properties.toRpcServiceName(),
                    new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), NettyRpcServer.PORT));
        } catch (UnknownHostException e) {
            log.error("occur exception when getHostAddress", e);
        }
    }

    @Override
    public void publishService(Object service, RpcServiceProperties properties) {
        publishService(service, service.getClass().getInterfaces()[0], properties);
    }

    @Override
    public void publishService(Object service) {
        publishService(service, RpcServiceProperties.builder()
                .group(RpcServiceProperties.DEFAULT_GROUP)
                .version(RpcServiceProperties.DEFAULT_VERSION).build());
    }
}
