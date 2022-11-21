package com.kalew515.registry.zk;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.registry.ServiceRegistry;
import com.kalew515.remoting.zookeeper.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_REGISTER_CENTER_ADDRESS;

public class ZkServiceRegistryImpl implements ServiceRegistry {

    private final String registerCenterAddress;
    private final ConfigCenter configCenter;

    public ZkServiceRegistryImpl () {
        configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        registerCenterAddress = configCenter.getConfig(RPC_REGISTER_CENTER_ADDRESS);
    }

    public ZkServiceRegistryImpl (String registerCenterAddress, ConfigCenter configCenter) {
        this.registerCenterAddress = registerCenterAddress;
        this.configCenter = configCenter;
    }

    @Override
    public void registerService (String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath =
                CuratorUtil.ZK_REGISTER_ROOT_PATH + CuratorUtil.SERVICE_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtil.getZkClient(registerCenterAddress);
        CuratorUtil.createEphemeralNode(zkClient, servicePath, new byte[]{(byte) '0'});
    }
}
