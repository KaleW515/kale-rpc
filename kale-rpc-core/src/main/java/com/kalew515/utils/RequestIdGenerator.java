package com.kalew515.utils;

import java.util.concurrent.atomic.AtomicInteger;

public class RequestIdGenerator {
    private static final AtomicInteger id = new AtomicInteger(12);

    public static int getRequestId () {
        return id.incrementAndGet();
    }
}
