package com.kalew515.cluster.loadbalancer;

import com.kalew515.cluster.AbstractLoadBalance;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.monitor.MonitorCenter;
import com.kalew515.monitor.MonitorCenterImpl;

import java.util.List;

public class MinConnLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest) {
        MonitorCenter monitorCenter = SingletonFactory.getInstance(MonitorCenterImpl.class);

        int min = Integer.MAX_VALUE;
        String url = null;
        for (String serverUrl : serviceAddresses) {
            Integer connectionTimes = monitorCenter.getConnectionTimes(serverUrl);
            if (connectionTimes < min) {
                min = connectionTimes;
                url = serverUrl;
            }
        }
        return url;
    }
}
