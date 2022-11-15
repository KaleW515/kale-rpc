package com.kalew515.registry;

import com.kalew515.common.extension.SPI;

import java.net.InetSocketAddress;

/**
 * service registry interface, this is an internal interface
 */
@SPI
public interface ServiceRegistry {
    void registerService (String rpcServiceName, InetSocketAddress inetSocketAddress);
}
