package com.kalew515.common.annotation;

import com.kalew515.common.spring.KaleRpcComponentScanRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * kale rpc component scan annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(KaleRpcComponentScanRegistrar.class)
public @interface KaleRpcComponentScan {

    String[] basePackages () default {};

    Class<?>[] basePackageClasses () default {};
}
