package com.kalew515.monitor;

/**
 * monitor center interface, for external invocation
 */
public interface MonitorCenter {

    /**
     * report connection message to monitor center
     *
     * @param localHost
     * @param remoteHost
     */
    void reportConn (String localHost, String remoteHost);

    /**
     * report device register to monitor center
     *
     * @param host
     */
    void reportDeviceRegister (String host);

    /**
     * report disconnection message to monitor center
     *
     * @param localHost
     * @param remoteHost
     */
    void reportDisconn (String localHost, String remoteHost);

    /**
     * report rpc to monitor center
     *
     * @param serviceName
     * @param address
     */
    void reportRpc (String serviceName, String address);

    /**
     * get connection times from monitor center
     *
     * @param remoteUrl
     * @return
     */
    Integer getConnectionTimes (String remoteUrl);

    /**
     * get service call times from monitor center
     *
     * @param serviceName
     * @param remoteUtl
     * @return
     */
    Integer getServiceCallTimes (String serviceName, String remoteUtl);
}
