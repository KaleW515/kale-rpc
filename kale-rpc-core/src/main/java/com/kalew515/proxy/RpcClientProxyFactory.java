package com.kalew515.proxy;

import com.kalew515.config.RpcServiceConfig;
import com.kalew515.remoting.transport.RpcClientFactory;

public class RpcClientProxyFactory {


    public static <T> T getProxy (RpcServiceConfig<T> rpcServiceConfig) {
        RpcClientProxy rpcClientProxy = new RpcClientProxy(RpcClientFactory.getRpcClient(),
                                                           rpcServiceConfig);
        return (T) rpcClientProxy.getProxy(rpcServiceConfig.getClazz());
    }
}
