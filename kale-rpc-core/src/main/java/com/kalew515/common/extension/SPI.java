package com.kalew515.common.extension;

import java.lang.annotation.*;

/**
 * spi annotation, for load class
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SPI {
}
