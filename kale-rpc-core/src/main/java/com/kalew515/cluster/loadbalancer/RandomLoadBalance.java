package com.kalew515.cluster.loadbalancer;

import com.kalew515.cluster.AbstractLoadBalance;
import com.kalew515.exchange.messages.RpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        if (serviceAddresses.size() > 0) {
            return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
        }
        return null;
    }
}
