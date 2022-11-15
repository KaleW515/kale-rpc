package com.kalew515.spring.boot.config.subconfig;


public class ClientConfig {

    public String loadBalancer;

    public String serializer;

    public String compress;

    public String timeout;

    public String failStrategy;

    public String getLoadBalancer () {
        return loadBalancer;
    }

    public void setLoadBalancer (String loadBalancer) {
        this.loadBalancer = loadBalancer;
    }

    public String getSerializer () {
        return serializer;
    }

    public void setSerializer (String serializer) {
        this.serializer = serializer;
    }

    public String getCompress () {
        return compress;
    }

    public void setCompress (String compress) {
        this.compress = compress;
    }

    public String getTimeout () {
        return timeout;
    }

    public void setTimeout (String timeout) {
        this.timeout = timeout;
    }

    public String getFailStrategy () {
        return failStrategy;
    }

    public void setFailStrategy (String failStrategy) {
        this.failStrategy = failStrategy;
    }
}
