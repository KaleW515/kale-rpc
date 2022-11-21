package com.kalew515.common.spring;

import com.kalew515.common.annotation.RpcReference;
import com.kalew515.common.annotation.RpcService;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.proxy.RpcClientProxy;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import com.kalew515.remoting.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

public class ServiceAndReferenceBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private RegisterCenter registerCenter;

    private volatile RpcClient rpcClient;

    private ApplicationContext context;

    public ServiceAndReferenceBeanPostProcessor () {
    }

    @PostConstruct
    public void doParamInit () {
        this.registerCenter = SingletonFactory.getInstance(RegisterCenterImpl.class);
    }

    @Override
    public Object postProcessBeforeInitialization (Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            logger.info("[{}] is annotated with  [{}]", bean.getClass().getName(),
                        RpcService.class.getCanonicalName());
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            RpcServiceConfig<?> rpcServiceConfig = new RpcServiceConfig<>(rpcService.version(),
                                                                          rpcService.group(), bean,
                                                                          bean.getClass()
                                                                              .getInterfaces()[0]);
            registerCenter.storeService(rpcServiceConfig);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization (Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                // rpcClient lazy init
                rpcClientLazyInit();
                RpcServiceConfig<?> rpcServiceConfig = new RpcServiceConfig<>(
                        rpcReference.version(),
                        rpcReference.group(), null);
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }

    @Override
    public void setApplicationContext (ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    private void rpcClientLazyInit () {
        if (rpcClient == null) {
            synchronized (this) {
                if (rpcClient == null) {
                    rpcClient = context.getBean(RpcClient.class);
                }
            }
        }
    }
}
