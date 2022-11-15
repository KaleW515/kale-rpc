package com.kalew515.monitor;

import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_MONITOR_CENTER_NAME;

public class MonitorCenterImpl implements MonitorCenter {

    private final MonitorReport monitorReport;

    private final MonitorObtain monitorObtain;


    public MonitorCenterImpl () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String monitorCenterName = configCenter.getConfig(RPC_MONITOR_CENTER_NAME);
        this.monitorReport = ExtensionLoader.getExtensionLoader(MonitorReport.class)
                                            .getExtension(monitorCenterName);
        this.monitorObtain = ExtensionLoader.getExtensionLoader(MonitorObtain.class)
                                            .getExtension(monitorCenterName);
    }

    @Override
    public void reportConn (String localHost, String remoteHost) {
        this.monitorReport.reportConn(localHost, remoteHost);
    }

    @Override
    public void reportDeviceRegister (String host) {
        this.monitorReport.reportDeviceRegister(host);
    }

    @Override
    public void reportDisconn (String localHost, String remoteHost) {
        this.monitorReport.reportDisconn(localHost, remoteHost);
    }

    @Override
    public void reportRpc (String serviceName, String address) {
        this.monitorReport.reportRpc(serviceName, address);
    }

    @Override
    public Integer getConnectionTimes (String remoteUrl) {
        return this.monitorObtain.getConnectionTimes(remoteUrl);
    }

    @Override
    public Integer getServiceCallTimes (String serviceName, String remoteUtl) {
        return this.monitorObtain.getServiceCallTimes(serviceName, remoteUtl);
    }
}
