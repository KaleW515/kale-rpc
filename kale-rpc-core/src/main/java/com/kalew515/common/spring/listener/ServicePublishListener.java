package com.kalew515.common.spring.listener;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.common.spring.event.RpcServerStartedEvent;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Set;

public class ServicePublishListener implements ApplicationListener<RpcServerStartedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private RegisterCenter registerCenter;

    // to make sure service publish behind the rpc server started
    @Override
    public void onApplicationEvent (RpcServerStartedEvent event) {
        logger.info("receive rpc server started event");
        this.registerCenter = SingletonFactory.getInstance(RegisterCenterImpl.class);
        doPublishExport();
    }

    private void doPublishExport () {
        Set<RpcServiceConfig<?>> serviceToPublish = registerCenter.getServiceToPublish();
        for (RpcServiceConfig<?> toPublish : serviceToPublish) {
            registerCenter.publishService(toPublish);
        }
    }
}
