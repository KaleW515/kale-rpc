package com.kalew515.cluster.loadbalancer;

import com.kalew515.cluster.AbstractLoadBalance;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.monitor.MonitorCenter;
import com.kalew515.monitor.MonitorCenterImpl;

import java.util.List;
import java.util.Set;

public class MinCallLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest,
                               Set<String> blackList) {
        MonitorCenter monitorCenter = SingletonFactory.getInstance(MonitorCenterImpl.class);

        int min = Integer.MAX_VALUE;
        String url = null;
        for (String serverUrl : serviceAddresses) {
            if (!blackList.contains(serverUrl)) {
                Integer serviceCallTimes = monitorCenter.getServiceCallTimes(
                        rpcRequest.getRpcServiceName(), serverUrl);

                if (serviceCallTimes < min) {
                    min = serviceCallTimes;
                    url = serverUrl;
                }
            }
        }
        return url;
    }
}
