package com.kalew515.config;

import com.kalew515.common.extension.SPI;

/**
 * config container, The real entity that stores the config
 */
@SPI
public interface ConfigContainer {

    /**
     * get config from config container, you should call this by ConfigCenter.getConfig(String key)
     *
     * @param key
     * @return
     */
    String getConfig (String key);

    /**
     * set config to config container, you should call this by ConfigCenter.setConfig(String key,
     * Object value)
     *
     * @param key
     * @param value
     */
    void setConfig (String key, Object value);
}
