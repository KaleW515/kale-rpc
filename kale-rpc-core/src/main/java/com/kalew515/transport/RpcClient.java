package com.kalew515.transport;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * rpc client interface
 */
@SPI
public interface RpcClient {
    /**
     * send rpc request to server
     *
     * @param rpcRequest
     * @param inetSocketAddress
     * @return
     */
    CompletableFuture<RpcResponse<?>> sendRpcRequest (RpcRequest rpcRequest,
                                                      InetSocketAddress inetSocketAddress);

    /**
     * get service address from register center
     *
     * @param rpcRequest
     * @param blackList
     * @return
     */
    InetSocketAddress getServiceAddress (RpcRequest rpcRequest, Set<String> blackList);

    /**
     * set serializer and compress
     *
     * @param serializer
     * @param compress
     */
    public void setSerializationAndCompress (String serializer, String compress);
}
