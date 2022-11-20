package com.kalew515.proxy.failstrategy;

import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.proxy.FailStrategy;
import com.kalew515.proxy.context.RequestContext;

import static com.kalew515.common.enums.RpcResponseStatusEnum.FAIL_SAFE;

public class FailSafe implements FailStrategy {
    @Override
    public RpcResponse<?> strategy (RequestContext requestContext) {
        return new RpcResponse<>(
                requestContext.getRpcRequest().getRequestId(), null, FAIL_SAFE.getCode(),
                null);
    }
}
