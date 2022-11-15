package com.kalew515.cluster;

import com.kalew515.common.extension.SPI;
import com.kalew515.exchange.impl.RpcRequest;

import java.util.List;
import java.util.Set;

@SPI
public interface LoadBalance {

    /**
     * select service address from serviceUrlList, this will be determined by the load balancing
     * policy
     *
     * @param serviceUrlList
     * @param rpcRequest
     * @param blackList
     * @return
     */
    String selectServiceAddress (List<String> serviceUrlList, RpcRequest rpcRequest,
                                 Set<String> blackList);

}
