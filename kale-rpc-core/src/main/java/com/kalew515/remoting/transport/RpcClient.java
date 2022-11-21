package com.kalew515.remoting.transport;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;

import java.net.InetSocketAddress;
import java.util.List;
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
     * @return
     */
    List<String> getServiceAddress (RpcRequest rpcRequest);

    /**
     * set serializer and compress
     *
     * @param serializer
     * @param compress
     */
    public void setSerializationAndCompress (String serializer, String compress);
}
