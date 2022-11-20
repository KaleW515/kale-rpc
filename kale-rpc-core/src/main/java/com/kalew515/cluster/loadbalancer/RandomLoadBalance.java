package com.kalew515.cluster.loadbalancer;

import com.kalew515.cluster.AbstractLoadBalance;
import com.kalew515.exchange.messages.RpcRequest;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest,
                               Set<String> blackList) {
        Random random = new Random();
        while (serviceAddresses.size() > 0) {
            String url = serviceAddresses.get(random.nextInt(serviceAddresses.size()));
            if (blackList.contains(url)) {
                serviceAddresses.remove(url);
            } else {
                return url;
            }
        }
        return null;
    }
}
