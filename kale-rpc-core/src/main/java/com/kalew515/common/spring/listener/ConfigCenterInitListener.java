package com.kalew515.common.spring.listener;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.utils.NetUtils;
import com.kalew515.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

import static com.kalew515.config.constants.RpcConfigConstants.*;
import static com.kalew515.config.constants.defaultconfig.RpcDefaultConfig.DEFAULT_CONFIG_CENTER;
import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.DEFAULT_TRANSPORTER_SERVER_PORT;

@Order(Ordered.HIGHEST_PRECEDENCE + 20 + 2)
public class ConfigCenterInitListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent (ApplicationContextInitializedEvent event) {
        logger.info("ConfigCenterInitListener");
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();
        String useRemoteConfig = environment.getProperty(RPC_CONFIG_CENTER_USE_REMOTE_CONFIG);
        if (useRemoteConfig == null || !useRemoteConfig.equals("true")) {
            doConfigInit(environment);
        }
    }

    private void doConfigInit (ConfigurableEnvironment environment) {
        logger.info("do config init");
        String configCenterName = environment.getProperty(
                RPC_CONFIG_CENTER_NAME);
        if (StringUtils.isBlank(configCenterName)) {
            configCenterName = DEFAULT_CONFIG_CENTER;
        }
        ConfigCenterImpl configCenter = new ConfigCenterImpl(configCenterName);
        configCenter.setConfig(RPC_CONFIG_CENTER_NAME, configCenterName);
        if (!configCenter.getRefreshed()) {
            Map<String, String> configMap = getConfigMap();
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String value = environment.getProperty(entry.getKey());
                if (!StringUtils.isBlank(value)) {
                    configCenter.setConfig(entry.getKey(), value);
                } else {
                    configCenter.setConfig(entry.getKey(), entry.getValue());
                }
            }
        }
        doCustomConfigInit(environment, configCenter);
        configCenter.setRefreshed();
        SingletonFactory.addInstance(ConfigCenterImpl.class, configCenter);
    }

    private void doCustomConfigInit (ConfigurableEnvironment environment,
                                     ConfigCenter configCenter) {
        String port = environment.getProperty(SERVER_PORT);
        Integer portToBind = NetUtils.parsePort(port);
        if (portToBind == null) {
            portToBind = DEFAULT_TRANSPORTER_SERVER_PORT;
        }
        configCenter.setConfig(SERVER_PORT, portToBind);
    }

}
