package com.kalew515.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kale
 * @date 2022/11/20 上午12:06
 */
public class RequestIdGeneratorUtils {

    private static final AtomicLong id = new AtomicLong(0);

    public static Long getRequestId () {
        return id.incrementAndGet();
    }
}
