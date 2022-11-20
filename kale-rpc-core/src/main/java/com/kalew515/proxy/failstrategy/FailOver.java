package com.kalew515.proxy.failstrategy;

import com.kalew515.common.exception.RpcException;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.proxy.FailStrategy;
import com.kalew515.proxy.RpcClientProxy;
import com.kalew515.proxy.context.RequestContext;
import com.kalew515.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE;

public class FailOver implements FailStrategy {

    private final Integer retryTimes = 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public RpcResponse<?> strategy (RequestContext requestContext) {
        Set<String> blackList = new HashSet<>();
        RpcClient rpcClient = requestContext.getRpcClient();
        InetSocketAddress lastAddress = requestContext.getInetSocketAddress();
        blackList.add(lastAddress.getHostName() + ":" + lastAddress.getPort());
        for (int i = 0; i < retryTimes; i++) {
            InetSocketAddress serviceAddress = rpcClient.getServiceAddress(
                    requestContext.getRpcRequest(), blackList);
            CompletableFuture<RpcResponse<?>> rpcResponse = rpcClient.sendRpcRequest(
                    requestContext.getRpcRequest(),
                    serviceAddress);
            try {
                RpcResponse<?> response = rpcResponse.get(requestContext.getTimeout(),
                                                          requestContext.getTimeUnit());
                RpcClientProxy.check(response, requestContext.getRpcRequest());
                return response;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException | TimeoutException e) {
                logger.warn(e.getMessage());
            }
        }
        throw new RpcException(SERVICE_INVOCATION_FAILURE);
    }
}
