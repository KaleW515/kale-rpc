package com.kalew515.exchange.local;

import com.kalew515.exchange.IdGenerator;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author kale
 * @date 2022/11/21 上午10:42
 */
public class LocalIdGenerator implements IdGenerator {

    private final AtomicLong id = new AtomicLong(0);

    @Override
    public Long generatorId () {
        return id.incrementAndGet();
    }
}
