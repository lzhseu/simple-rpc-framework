package top.lzhseu.annotation;

import java.lang.annotation.*;

/**
 * @author lzh
 * @date 2020/12/8 20:28
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcScan {

    String[] basePackage();
}
