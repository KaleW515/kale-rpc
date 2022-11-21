package com.kalew515.common.spring;

import com.kalew515.common.annotation.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class KaleRpcComponentScanRegistrar implements ImportBeanDefinitionRegistrar,
        ResourceLoaderAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions (AnnotationMetadata importingClassMetadata,
                                         BeanDefinitionRegistry registry) {
        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata);
        registerServiceAnnotationPostProcessor(packagesToScan, registry);
    }

    private void registerServiceAnnotationPostProcessor (Set<String> packagesToScan,
                                                         BeanDefinitionRegistry registry) {
        KaleRpcClassPathBeanDefinitionScanner rpcServiceScanner =
                new KaleRpcClassPathBeanDefinitionScanner(registry,
                                                          RpcService.class);
        if (resourceLoader != null) {
            rpcServiceScanner.setResourceLoader(resourceLoader);
        }
        int rpcServiceScanned = 0;
        for (String packageToScan : packagesToScan) {
            rpcServiceScanned += rpcServiceScanner.scan(packageToScan);
        }
        logger.info("rpcServiceScanner has scanned: [{}]", rpcServiceScanned);
    }

    private Set<String> getPackagesToScan (AnnotationMetadata metadata) {
        // get from @KaleRpcComponentScan
        Set<String> packagesToScan = getPackagesToScan0(metadata
        );

        if (packagesToScan.isEmpty()) {
            return Collections.singleton(ClassUtils.getPackageName(metadata.getClassName()));
        }
        return packagesToScan;
    }

    private Set<String> getPackagesToScan0 (AnnotationMetadata metadata) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(KaleRpcComponentScanRegistrar.class.getName()));
        if (attributes == null) {
            return Collections.emptySet();
        }
        // basePackages
        String[] basePackages = attributes.getStringArray("basePackages");
        Set<String> packagesToScan = new LinkedHashSet<>(Arrays.asList(basePackages));
        // basePackageClasses
        Class<?>[] basePackageClasses = attributes.getClassArray("basePackageClasses");
        for (Class<?> basePackageClass : basePackageClasses) {
            packagesToScan.add(ClassUtils.getPackageName(basePackageClass));
        }
        return packagesToScan;
    }

    @Override
    public void setResourceLoader (ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
