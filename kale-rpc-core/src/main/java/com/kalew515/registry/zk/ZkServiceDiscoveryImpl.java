package com.kalew515.registry.zk;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.registry.ServiceDiscovery;
import com.kalew515.remoting.zookeeper.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_REGISTER_CENTER_ADDRESS;

public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String registerCenterAddress;

    public ZkServiceDiscoveryImpl () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        registerCenterAddress = configCenter.getConfig(RPC_REGISTER_CENTER_ADDRESS);
    }

    @Override
    public List<String> lookupService (RpcRequest rpcRequest) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient(registerCenterAddress);
        return CuratorUtil.getServiceChildrenNodes(zkClient, rpcServiceName);
    }
}
