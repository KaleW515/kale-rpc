package com.kalew515.spring.boot.config;

import com.kalew515.spring.boot.config.subconfig.*;
import com.kalew515.spring.boot.util.KaleRpcUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = KaleRpcUtils.KALE_RPC)
public class KaleRpcProperties {

    public String transporter;

    @NestedConfigurationProperty
    public MonitorCenterConfig monitorCenter;

    @NestedConfigurationProperty
    public ServerConfig server;

    @NestedConfigurationProperty
    public ClientConfig client;

    @NestedConfigurationProperty
    public RegisterCenterConfig registerCenter;

    @NestedConfigurationProperty
    public ConfigCenterConfig configCenter;

    public String getTransporter () {
        return transporter;
    }

    public void setTransporter (String transporter) {
        this.transporter = transporter;
    }

    public MonitorCenterConfig getMonitorCenter () {
        return monitorCenter;
    }

    public void setMonitorCenter (MonitorCenterConfig monitorCenter) {
        this.monitorCenter = monitorCenter;
    }

    public ServerConfig getServer () {
        return server;
    }

    public void setServer (ServerConfig server) {
        this.server = server;
    }

    public ClientConfig getClient () {
        return client;
    }

    public void setClient (ClientConfig client) {
        this.client = client;
    }

    public RegisterCenterConfig getRegisterCenter () {
        return registerCenter;
    }

    public void setRegisterCenter (RegisterCenterConfig registerCenter) {
        this.registerCenter = registerCenter;
    }

    public ConfigCenterConfig getConfigCenter () {
        return configCenter;
    }

    public void setConfigCenter (ConfigCenterConfig configCenter) {
        this.configCenter = configCenter;
    }
}
