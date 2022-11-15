package com.kalew515.common.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

public class KaleRpcClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    public KaleRpcClassPathBeanDefinitionScanner (BeanDefinitionRegistry registry, Class<?
            extends Annotation> annoType) {
        super(registry);
        super.addIncludeFilter(new AnnotationTypeFilter(annoType));
    }

    @Override
    public int scan (String... basePackages) {
        return super.scan(basePackages);
    }
}
