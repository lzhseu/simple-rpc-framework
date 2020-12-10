package top.lzhseu.spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.stereotype.Component;
import top.lzhseu.annotation.RpcScan;
import top.lzhseu.annotation.RpcService;


/**
 * @author lzh
 * @date 2020/12/8 20:30
 */
@Slf4j
public class CustomBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private static final String BASE_PACKAGES_ATTRIBUTE_NAME = "basePackages";

    private static final String SPRING_BEAN_BASE_PACKAGE = "top.lzhseu.spring";


    private ResourceLoader resourceLoader;


    /**
     * @param importingClassMetadata 当前被 @Import 注解给标注的所有注解信息
     * @param registry 用于注册定义一个 bean
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        // 获取 RpcScan 注解的属性和值
        AnnotationAttributes rpcScanAnnotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(RpcScan.class.getName()));

        String[] rpcScanBasePackages = new String[0];

        if (rpcScanAnnotationAttributes != null) {
            // 获取 basePackage 的值
            rpcScanBasePackages = rpcScanAnnotationAttributes.getStringArray(BASE_PACKAGES_ATTRIBUTE_NAME);
        }

        if (rpcScanBasePackages.length == 0) {
            // 那就返回此注解所在的包
            rpcScanBasePackages = new String[]{((StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }

        // 扫描 RpcService 注解
        CustomScanner rpcServiceScanner = new CustomScanner(registry, RpcService.class);

        // 扫描 Component 注解
        CustomScanner springBeanScanner = new CustomScanner(registry, Component.class);

        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
            springBeanScanner.setResourceLoader(resourceLoader);
        }

        // 扫描 top.lzhseu.spring 包下的 @Component 注解
        int springBeanAmount = springBeanScanner.scan(SPRING_BEAN_BASE_PACKAGE);
        log.info("springBeanScanner 扫描的数量 [{}]", springBeanAmount);

        // 扫描 rpcScanBasePackages 包下的 @RpcService 注解
        int rpcServiceAmount = rpcServiceScanner.scan(rpcScanBasePackages);
        log.info("rpcServiceScanner 扫描的数量 [{}]", rpcServiceAmount);
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
