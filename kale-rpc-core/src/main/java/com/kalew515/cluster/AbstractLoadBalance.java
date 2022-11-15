package com.kalew515.cluster;

import com.kalew515.exchange.impl.RpcRequest;
import com.kalew515.utils.CollectionUtil;

import java.util.List;
import java.util.Set;

public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public String selectServiceAddress (List<String> serviceUrlList,
                                        RpcRequest rpcRequest,
                                        Set<String> blackList) {
        if (CollectionUtil.isEmpty(serviceUrlList)) return null;
        if (serviceUrlList.size() == 1) {
            String url = serviceUrlList.get(0);
            for (String inetSocketAddress : blackList) {
                if (url.equals(inetSocketAddress)) {
                    return null;
                }
            }
            return serviceUrlList.get(0);
        }
        return doSelect(serviceUrlList, rpcRequest, blackList);
    }

    protected abstract String doSelect (List<String> serviceAddresses, RpcRequest rpcRequest,
                                        Set<String> blackList);
}
