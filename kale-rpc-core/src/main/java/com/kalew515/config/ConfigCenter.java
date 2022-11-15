package com.kalew515.config;

/**
 * config center interface, for external invocation
 */
public interface ConfigCenter {

    /**
     * get config from config container
     *
     * @param key
     * @return
     */
    String getConfig (String key);

    /**
     * set config to config container
     *
     * @param key
     * @param value
     */
    void setConfig (String key, Object value);
}
