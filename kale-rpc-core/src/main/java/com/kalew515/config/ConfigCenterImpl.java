package com.kalew515.config;

import com.kalew515.common.enums.ConfigCenterEnum;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.config.constants.RpcConfigConstants;
import com.kalew515.utils.NetUtils;
import com.kalew515.utils.StringUtils;
import com.kalew515.utils.YmlPropertiesConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_CONFIG_CENTER_NAME;
import static com.kalew515.config.constants.RpcConfigConstants.SERVER_PORT;
import static com.kalew515.config.constants.defaultconfig.RpcDefaultConfig.DEFAULT_CONFIG_CENTER;
import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.DEFAULT_TRANSPORTER_SERVER_PORT;

public class ConfigCenterImpl implements ConfigCenter {

    private final ConfigContainer configContainer;
    private final String configContainerName;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private boolean refreshed = false;

    public ConfigCenterImpl () {
        Properties ymlProperties = YmlPropertiesConfigUtil.getYmlProperties();
        String configCenterName = ymlProperties.getProperty(RPC_CONFIG_CENTER_NAME);
        this.configContainerName = getConfigCenterName(configCenterName);
        this.configContainer = ExtensionLoader.getExtensionLoader(ConfigContainer.class)
                                              .getExtension(configContainerName);
        doConfigInit(ymlProperties);
    }

    public ConfigCenterImpl (String configContainerName) {
        this.configContainerName = configContainerName;
        this.configContainer = ExtensionLoader.getExtensionLoader(ConfigContainer.class)
                                              .getExtension(configContainerName);
    }

    private String getConfigCenterName (String configCenterName) {
        if (!StringUtils.isBlank(configCenterName)) {
            for (ConfigCenterEnum value : ConfigCenterEnum.values()) {
                if (configCenterName.equals(value.getName())) {
                    return value.getName();
                }
            }
        }
        return DEFAULT_CONFIG_CENTER;
    }

    @Override
    public String getConfig (String key) {
        return this.configContainer.getConfig(key);
    }

    @Override
    public void setConfig (String key, Object value) {
        logger.info("key: [{}], value: [{}]", key, value);
        this.configContainer.setConfig(key, value);
    }


    public void setRefreshed () {
        this.refreshed = true;
    }

    public boolean getRefreshed () {
        return refreshed;
    }

    private void doConfigInit (Properties ymlProperties) {
        if (!refreshed) {
            Map<String, String> configMap = RpcConfigConstants.getConfigMap();
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String property = ymlProperties.getProperty(entry.getKey());
                if (StringUtils.isBlank(property)) {
                    this.configContainer.setConfig(entry.getKey(), entry.getValue());
                } else {
                    this.configContainer.setConfig(entry.getKey(), property);
                }
            }
            String port = ymlProperties.getProperty(SERVER_PORT);
            Integer portToBind = NetUtils.parsePort(port);
            if (portToBind == null) {
                portToBind = DEFAULT_TRANSPORTER_SERVER_PORT;
            }
            this.configContainer.setConfig(SERVER_PORT, portToBind);
        }
        refreshed = true;
    }
}
