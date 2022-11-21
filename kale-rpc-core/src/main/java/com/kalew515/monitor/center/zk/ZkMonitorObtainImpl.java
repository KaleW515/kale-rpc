package com.kalew515.monitor.center.zk;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.monitor.MonitorObtain;
import com.kalew515.remoting.zookeeper.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_MONITOR_CENTER_ADDRESS;

public class ZkMonitorObtainImpl implements MonitorObtain {

    private final String monitorCenterAddress;

    public ZkMonitorObtainImpl () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        monitorCenterAddress = configCenter.getConfig(RPC_MONITOR_CENTER_ADDRESS);
    }

    @Override
    public Integer getConnectionTimes (String remoteUrl) {
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        List<String> list = CuratorUtil.getDeviceChildrenNodes(zkClient, remoteUrl.split(":")[0]);
        return list == null ? 0 : list.size();
    }

    @Override
    public Integer getServiceCallTimes (String serviceName, String remoteUtl) {
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        return CuratorUtil.getServiceCallTimes(zkClient,
                                               serviceName,
                                               remoteUtl);
    }
}
