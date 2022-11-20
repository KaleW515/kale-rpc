package com.kalew515.registry;

import com.kalew515.config.RpcServiceConfig;
import com.kalew515.exchange.messages.RpcRequest;

import java.net.InetSocketAddress;
import java.util.Set;

public interface RegisterCenter {

    /**
     * return service object to handler request
     *
     * @param rpcServiceName
     * @return
     */
    Object getService (String rpcServiceName);

    /**
     * publish service to register center
     *
     * @param rpcServiceConfig
     */
    void publishService (RpcServiceConfig<?> rpcServiceConfig);

    /**
     * temporarily stored to map, waiting for rpc server started
     *
     * @param rpcServiceConfig
     */
    void storeService (RpcServiceConfig<?> rpcServiceConfig);

    /**
     * get stored services, then publish
     *
     * @return
     */
    Set<RpcServiceConfig<?>> getServiceToPublish ();

    /**
     * lookup service from register center
     *
     * @param rpcRequest
     * @param blackList
     * @return
     */
    InetSocketAddress lookupService (RpcRequest rpcRequest, Set<String> blackList);

}
