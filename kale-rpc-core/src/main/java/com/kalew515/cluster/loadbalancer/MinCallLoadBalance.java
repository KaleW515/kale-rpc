package com.kalew515.cluster.loadbalancer;

import com.kalew515.cluster.AbstractLoadBalance;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.exchange.impl.RpcRequest;
import com.kalew515.utils.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.util.List;
import java.util.Set;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_MONITOR_CENTER_ADDRESS;

public class MinCallLoadBalance extends AbstractLoadBalance {

    @Override
    protected String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest,
                               Set<String> blackList) {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String address = configCenter.getConfig(RPC_MONITOR_CENTER_ADDRESS);
        CuratorFramework zkClient = CuratorUtil.getZkClient(address);

        int min = Integer.MAX_VALUE;
        String url = null;
        for (String serverUrl : serviceAddresses) {
            if (!blackList.contains(serverUrl)) {
                int times = CuratorUtil.getDeviceServiceCallTimes(zkClient,
                                                                  rpcRequest.getRpcServiceName(),
                                                                  serverUrl);
                if (times < min) {
                    min = times;
                    url = serverUrl;
                }
            }
        }
        return url;
    }
}
