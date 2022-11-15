package com.kalew515.config;

public class RpcServiceConfig<T> {
    private String version = "";

    private String group = "";

    private Object service;

    private Class<T> clazz;

    public RpcServiceConfig () {
    }

    public RpcServiceConfig (String version, String group, Class<T> clazz) {
        this(version, group, null, clazz);
    }

    public RpcServiceConfig (String version, String group, Object service, Class<T> clazz) {
        this.version = version;
        this.group = group;
        this.service = service;
        this.clazz = clazz;
    }

    public String getRpcServiceName () {
        return this.getServiceName() + this.getGroup() + this.getVersion();
    }

    public String getServiceName () {
        return this.clazz.getCanonicalName();
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public String getGroup () {
        return group;
    }

    public void setGroup (String group) {
        this.group = group;
    }

    public Object getService () {
        return service;
    }

    public void setService (Object service) {
        this.service = service;
    }

    public Class<?> getClazz () {
        return clazz;
    }

    public void setClazz (Class<T> clazz) {
        this.clazz = clazz;
    }
}
