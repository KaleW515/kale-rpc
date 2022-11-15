package com.kalew515.common.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class SingletonFactory {

    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<>();

    private SingletonFactory () {
    }

    public static <T> T getInstance (Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException();
        }
        String key = clazz.getName();
        if (OBJECT_MAP.containsKey(key)) {
            return clazz.cast(OBJECT_MAP.get(key));
        } else {
            return clazz.cast(OBJECT_MAP.computeIfAbsent(key, k -> {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException |
                         InvocationTargetException | NoSuchMethodException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }));
        }
    }

    public static <T> void addInstance (Class<T> c, T obj) {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        String key = c.getName();
        OBJECT_MAP.putIfAbsent(key, obj);
    }
}
