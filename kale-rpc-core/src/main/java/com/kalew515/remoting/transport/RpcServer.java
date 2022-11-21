package com.kalew515.transport;

import com.kalew515.common.enums.RpcServerStateEnum;
import com.kalew515.common.extension.SPI;
import com.kalew515.config.RpcServiceConfig;

/**
 * rpc server interface
 */
@SPI
public interface RpcServer {

    /**
     * start server
     */
    public void start ();

    /**
     * start server with custom host and port
     *
     * @param host
     * @param port
     */
    public void start (String host, int port);

    /**
     * register service
     *
     * @param rpcServiceConfig
     */
    public void registerService (RpcServiceConfig<?> rpcServiceConfig);

    /**
     * get server state
     *
     * @return
     */
    public RpcServerStateEnum getServerState ();

    /**
     * set server host and port
     *
     * @param host
     * @param port
     */
    public void setServerHostAndPort (String host, Integer port);
}
