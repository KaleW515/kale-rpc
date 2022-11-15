package com.kalew515.compress.dont;

import com.kalew515.compress.Compress;

public class DoNotCompress implements Compress {
    @Override
    public byte[] compress (byte[] bytes) {
        return bytes;
    }

    @Override
    public byte[] decompress (byte[] bytes) {
        return bytes;
    }
}
