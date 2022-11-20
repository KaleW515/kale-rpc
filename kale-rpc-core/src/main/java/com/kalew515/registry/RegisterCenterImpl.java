package com.kalew515.registry;

import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.exchange.messages.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND;
import static com.kalew515.config.constants.RpcConfigConstants.*;

public class RegisterCenterImpl implements RegisterCenter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    private final Set<RpcServiceConfig<?>> serviceToPublish = ConcurrentHashMap.newKeySet();

    private final ServiceRegistry serviceRegistry;

    private final ServiceDiscovery serviceDiscovery;

    private final ConfigCenter configCenter;

    public RegisterCenterImpl () {
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String registerCenterName = configCenter.getConfig(RPC_REGISTER_CENTER_NAME);
        this.serviceRegistry = ExtensionLoader.getExtensionLoader(ServiceRegistry.class)
                                              .getExtension(registerCenterName);
        this.serviceDiscovery = ExtensionLoader.getExtensionLoader(ServiceDiscovery.class)
                                               .getExtension(registerCenterName);
    }

    @Override
    public Object getService (String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(SERVICE_CAN_NOT_BE_FOUND);
        }
        return service;
    }

    @Override
    public void publishService (RpcServiceConfig<?> rpcServiceConfig) {
        String host = configCenter.getConfig(SERVER_HOST);
        int port = Integer.parseInt(configCenter.getConfig(SERVER_PORT));
        this.addService(rpcServiceConfig);
        serviceRegistry.registerService(rpcServiceConfig.getRpcServiceName(),
                                        new InetSocketAddress(host, port));
    }

    @Override
    public void storeService (RpcServiceConfig<?> rpcServiceConfig) {
        if (serviceToPublish.contains(rpcServiceConfig)) {
            return;
        }
        serviceToPublish.add(rpcServiceConfig);
        logger.info("store service: {} and interfaces:{}", rpcServiceConfig.getRpcServiceName(),
                    rpcServiceConfig.getService()
                                    .getClass()
                                    .getInterfaces());
    }

    @Override
    public InetSocketAddress lookupService (RpcRequest rpcRequest, Set<String> blackList) {
        return this.serviceDiscovery.lookupService(rpcRequest, blackList);
    }

    private void addService (RpcServiceConfig<?> rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (registeredService.contains(rpcServiceName)) {
            return;
        }
        registeredService.add(rpcServiceName);
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
        logger.info("Add service: {} and interfaces:{}", rpcServiceName,
                    rpcServiceConfig.getService()
                                    .getClass()
                                    .getInterfaces());
    }

    public Set<RpcServiceConfig<?>> getServiceToPublish () {
        return serviceToPublish;
    }
}
