package com.kalew515.proxy;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.proxy.context.RequestContext;

/**
 * fail strategy interface, for rpc fail
 */
@SPI
public interface FailStrategy {

    RpcResponse<?> strategy (RequestContext requestContext);
}
