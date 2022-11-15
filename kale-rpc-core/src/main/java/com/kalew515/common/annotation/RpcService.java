package com.kalew515.common.annotation;

import java.lang.annotation.*;

/**
 * rpc service annotation, for provider
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    String version () default "";

    String group () default "";
}
