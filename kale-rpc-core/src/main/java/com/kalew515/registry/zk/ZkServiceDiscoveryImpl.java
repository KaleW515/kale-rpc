package com.kalew515.registry.zk;

import com.kalew515.cluster.LoadBalance;
import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.registry.ServiceDiscovery;
import com.kalew515.utils.CollectionUtil;
import com.kalew515.utils.CuratorUtil;
import com.kalew515.utils.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND;
import static com.kalew515.config.constants.RpcConfigConstants.RPC_LOAD_BALANCER;
import static com.kalew515.config.constants.RpcConfigConstants.RPC_REGISTER_CENTER_ADDRESS;

public class ZkServiceDiscoveryImpl implements ServiceDiscovery {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LoadBalance loadBalance;

    private final String registerCenterAddress;

    public ZkServiceDiscoveryImpl () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String loadBalancer = configCenter.getConfig(RPC_LOAD_BALANCER);
        logger.info("loadBalancer is [{}]", loadBalancer);
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                                          .getExtension(loadBalancer);
        registerCenterAddress = configCenter.getConfig(RPC_REGISTER_CENTER_ADDRESS);
    }

    @Override
    public InetSocketAddress lookupService (RpcRequest rpcRequest,
                                            Set<String> blackList) {
        String rpcServiceName = rpcRequest.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtil.getZkClient(registerCenterAddress);
        List<String> serviceUrlList = CuratorUtil.getServiceChildrenNodes(zkClient, rpcServiceName);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // load balancing
        String targetServiceUrl;
        logger.info("loadBalancer is " + loadBalance.getClass().getCanonicalName());
        targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList,
                                                            rpcRequest,
                                                            blackList);
        logger.info("Successfully found the service address:[{}]", targetServiceUrl);
        if (StringUtils.isBlank(targetServiceUrl)) {
            throw new RpcException(SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String[] socketAddressArray = targetServiceUrl.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
