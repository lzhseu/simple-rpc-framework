package top.lzhseu.annotation;

import org.springframework.context.annotation.Import;
import top.lzhseu.spring.CustomBeanDefinitionRegistrar;

import java.lang.annotation.*;

/**
 * @author lzh
 * @date 2020/12/8 21:22
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomBeanDefinitionRegistrar.class)
public @interface RpcScan {

    String[] basePackages();
}
