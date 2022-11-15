package com.kalew515.monitor;

import com.kalew515.common.extension.SPI;

/**
 * monitor obtain interface, this is an internal interface
 */
@SPI
public interface MonitorObtain {

    Integer getConnectionTimes (String remoteUrl);

    Integer getServiceCallTimes (String serviceName, String remoteUtl);

}
