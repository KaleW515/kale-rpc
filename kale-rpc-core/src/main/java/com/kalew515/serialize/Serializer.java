package com.kalew515.serialize;

import com.kalew515.common.extension.SPI;

/**
 * serializer interface
 */
@SPI
public interface Serializer {

    byte[] serialize (Object obj);

    <T> T deserialize (byte[] bytes, Class<T> clazz);
}
