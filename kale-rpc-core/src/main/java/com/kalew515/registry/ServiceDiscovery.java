package com.kalew515.registry;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.messages.RpcRequest;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

/**
 * service discovery interface, this is an internal interface
 */
@SPI
public interface ServiceDiscovery {

    List<String> lookupService (RpcRequest rpcRequest);

}
