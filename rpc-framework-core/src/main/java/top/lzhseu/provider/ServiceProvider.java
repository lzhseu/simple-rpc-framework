package top.lzhseu.provider;

import top.lzhseu.entity.RpcServiceProperties;

/**
 * 存储/提供服务对象
 *
 * @author lzh
 * @date 2020/12/8 9:52
 */
public interface ServiceProvider {

    /**
     * 添加服务
     * @param service 服务对象
     * @param serviceClass 服务对象实现的接口
     * @param properties 服务对象属性
     */
    void addService(Object service, Class<?> serviceClass, RpcServiceProperties properties);


    /**
     * 获取服务
     * @param properties 服务对象属性
     * @return 服务对象
     */
    Object getService(RpcServiceProperties properties);

    /**
     * 发布服务
     * @param service 服务对象
     * @param serviceClass 服务对象所实现的接口
     * @param properties 服务对象属性
     */
    void publishService(Object service, Class<?> serviceClass, RpcServiceProperties properties);

    /**
     * 发布服务，默认服务接口为服务对象实现的第一个接口
     * @param service 服务对象
     * @param properties 服务对象属性
     */
    void publishService(Object service, RpcServiceProperties properties);

    /**
     * 发布服务，默认服务接口为服务对象实现的第一个接口，且服务对象属性为默认值
     * @param service 服务对象
     */
    void publishService(Object service);
}
