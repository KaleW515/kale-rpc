package com.kalew515.cluster.failstrategy;

import com.kalew515.common.exception.RpcException;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.cluster.FailStrategy;
import com.kalew515.cluster.context.RequestContext;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE;

public class FailFast implements FailStrategy {
    @Override
    public RpcResponse<?> strategy (RequestContext requestContext) {
        throw new RpcException(SERVICE_INVOCATION_FAILURE);
    }
}
