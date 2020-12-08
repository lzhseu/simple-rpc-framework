package top.lzhseu.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import top.lzhseu.annotation.RpcReference;
import top.lzhseu.annotation.RpcService;
import top.lzhseu.entity.RpcServiceProperties;
import top.lzhseu.provider.ServiceProvider;
import top.lzhseu.provider.impl.ServiceProviderImpl;
import top.lzhseu.proxy.RpcClientProxy;
import top.lzhseu.remoting.transport.RpcRequestTransport;
import top.lzhseu.remoting.transport.netty.client.NettyRpcClient;

import java.lang.reflect.Field;

/**
 * @author lzh
 * @date 2020/12/8 19:28
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider = ServiceProviderImpl.getInstance();

    private final RpcRequestTransport rpcClient = new NettyRpcClient();

    /**
     * 每个 Spring bean 实例化之前，先关注一下是否注解了 @RpcService，是的话则自动注册服务
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {

            log.info("[{}] is annotated with [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());

            RpcService anno = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceProperties properties = RpcServiceProperties.builder()
                    .group(anno.group())
                    .version(anno.version()).build();

            serviceProvider.publishService(bean, properties);
        }
        return bean;
    }

    /**
     * 每个 Spring bean 实例化之后，会检查其所有的实例域，如果注解了 @RpcReference，则将该域设置为代理对象
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        Field[] declaredFields = bean.getClass().getDeclaredFields();

        for (Field field : declaredFields) {

            RpcReference anno = field.getAnnotation(RpcReference.class);

            if (anno != null) {
                RpcServiceProperties properties = RpcServiceProperties.builder()
                        .group(anno.group())
                        .version((anno.version())).build();

                RpcClientProxy proxy = new RpcClientProxy(rpcClient, properties);
                Object clientProxy = proxy.getProxy(field.getType());
                field.setAccessible(true);

                try {
                    field.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return bean;
    }
}
