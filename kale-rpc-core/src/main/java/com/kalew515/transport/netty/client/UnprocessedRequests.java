package com.kalew515.transport.netty.client;

import com.kalew515.exchange.impl.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequests {

    private static final Map<Integer, CompletableFuture<RpcResponse<?>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put (int requestId, CompletableFuture<RpcResponse<?>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete (RpcResponse<?> rpcResponse) {
        CompletableFuture<RpcResponse<?>> future = UNPROCESSED_RESPONSE_FUTURES.remove(
                rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else throw new IllegalStateException();
    }
}
