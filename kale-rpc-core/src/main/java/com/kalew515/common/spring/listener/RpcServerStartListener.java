package com.kalew515.common.spring.listener;

import com.kalew515.common.annotation.RpcService;
import com.kalew515.common.enums.RpcServerStateEnum;
import com.kalew515.common.spring.event.RpcServerStartedEvent;
import com.kalew515.remoting.transport.RpcServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


public class RpcServerStartListener implements ApplicationListener<ContextRefreshedEvent>,
        ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent (ContextRefreshedEvent event) {
        String[] beanNamesForAnnotation =
                applicationContext.getBeanNamesForAnnotation(RpcService.class);
        if (beanNamesForAnnotation.length > 0) {
            RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
            if (rpcServer.getServerState() == RpcServerStateEnum.PREPARED) {
                rpcServer.start();
                logger.info("rpc server started");
            }
        }
        applicationContext.publishEvent(new RpcServerStartedEvent(this));
    }

    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
