package com.kalew515.proxy.context;

import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.transport.RpcClient;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class RequestContext {

    private final RpcRequest rpcRequest;

    private final InetSocketAddress inetSocketAddress;

    private final Integer timeout;

    private final TimeUnit timeUnit;

    private final RpcClient rpcClient;

    public RequestContext (RpcClient rpcClient, RpcRequest rpcRequest,
                           InetSocketAddress inetSocketAddress,
                           Integer timeout, TimeUnit timeUnit) {
        this.rpcClient = rpcClient;
        this.rpcRequest = rpcRequest;
        this.inetSocketAddress = inetSocketAddress;
        this.timeout = timeout;
        this.timeUnit = timeUnit;
    }

    public RpcRequest getRpcRequest () {
        return rpcRequest;
    }

    public InetSocketAddress getInetSocketAddress () {
        return inetSocketAddress;
    }

    public Integer getTimeout () {
        return timeout;
    }

    public TimeUnit getTimeUnit () {
        return timeUnit;
    }

    public RpcClient getRpcClient () {
        return rpcClient;
    }
}
