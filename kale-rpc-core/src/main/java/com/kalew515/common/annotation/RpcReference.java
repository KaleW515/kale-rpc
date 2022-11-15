package com.kalew515.common.annotation;

import java.lang.annotation.*;

/**
 * rpc reference annotation, for consumer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    String version () default "";

    String group () default "";
}
