package com.kalew515.cluster;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.cluster.context.RequestContext;

/**
 * fail strategy interface, for rpc fail
 */
@SPI
public interface FailStrategy {

    RpcResponse<?> strategy (RequestContext requestContext);
}
