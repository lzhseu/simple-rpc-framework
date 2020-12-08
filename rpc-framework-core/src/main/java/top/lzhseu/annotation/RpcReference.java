package top.lzhseu.annotation;

import top.lzhseu.entity.RpcServiceProperties;

import java.lang.annotation.*;

/**
 * 消费服务的注解
 *
 * @author lzh
 * @date 2020/12/8 19:14
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcReference {

    String group() default RpcServiceProperties.DEFAULT_GROUP;

    String version() default RpcServiceProperties.DEFAULT_VERSION;
}
