package com.kalew515.monitor;

import com.kalew515.common.extension.SPI;

/**
 * monitor report interface, this is an internal interface
 */
@SPI
public interface MonitorReport {

    void reportConn (String localHost, String remoteHost);

    void reportDeviceRegister (String host);

    void reportDisconn (String localHost, String remoteHost);

    void reportRpc (String serviceName, String address);
}
