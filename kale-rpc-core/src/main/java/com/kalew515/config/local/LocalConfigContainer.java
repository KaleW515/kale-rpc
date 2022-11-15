package com.kalew515.config.local;

import com.kalew515.config.ConfigContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LocalConfigContainer implements ConfigContainer {

    private final Map<String, Object> config = new ConcurrentHashMap<>();

    @Override
    public String getConfig (String key) {
        if (config.containsKey(key)) {
            return config.get(key).toString();
        }
        return null;
    }

    @Override
    public void setConfig (String key, Object value) {
        config.put(key, value);
    }
}
