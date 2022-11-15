package com.kalew515.spring.boot.autoconfigure;

import com.kalew515.common.spring.SpringBeanPostProcessor;
import com.kalew515.spring.boot.config.KaleRpcProperties;
import com.kalew515.spring.boot.util.KaleRpcUtils;
import com.kalew515.transport.RpcClientFactory;
import com.kalew515.transport.RpcServerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = KaleRpcUtils.KALE_RPC, name = "enabled", matchIfMissing = true)
@EnableConfigurationProperties({KaleRpcProperties.class})
public class KaleRpcAutoConfiguration {

    @Bean
    public RpcServerFactory rpcServerFactory () {
        return new RpcServerFactory();
    }

    @Bean
    public RpcClientFactory rpcClientFactory () {
        return new RpcClientFactory();
    }

    @Bean
    @ConditionalOnBean(value = {RpcClientFactory.class, RpcServerFactory.class})
    public SpringBeanPostProcessor springBeanPostProcessor () {
        return new SpringBeanPostProcessor();
    }
}
