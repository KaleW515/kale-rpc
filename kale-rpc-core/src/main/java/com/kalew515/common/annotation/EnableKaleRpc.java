package com.kalew515.common.annotation;


import java.lang.annotation.*;

/**
 * enable kale rpc, core annotation
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@KaleRpcComponentScan
public @interface EnableKaleRpc {
}
