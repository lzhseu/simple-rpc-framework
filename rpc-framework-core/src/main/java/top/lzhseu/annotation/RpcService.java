package top.lzhseu.annotation;

import org.springframework.stereotype.Component;
import top.lzhseu.entity.RpcServiceProperties;

import java.lang.annotation.*;

/**
 * 注册服务的注解，在服务实现类上标记
 *
 * @author lzh
 * @date 2020/12/8 19:14
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcService {

    String group() default RpcServiceProperties.DEFAULT_GROUP;

    String version() default  RpcServiceProperties.DEFAULT_VERSION;
}
