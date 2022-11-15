package com.kalew515.compress;

import com.kalew515.common.extension.SPI;

/**
 * compress, help compress message
 */
@SPI
public interface Compress {

    byte[] compress (byte[] bytes);

    byte[] decompress (byte[] bytes);
}
