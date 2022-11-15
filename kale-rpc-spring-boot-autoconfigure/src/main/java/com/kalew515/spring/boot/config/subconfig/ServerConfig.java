package com.kalew515.spring.boot.config.subconfig;

public class ServerConfig {

    public Integer port;

    public String mq;

    public String mqAddress;

    public Integer getPort () {
        return port;
    }

    public void setPort (Integer port) {
        this.port = port;
    }

    public String getMq () {
        return mq;
    }

    public void setMq (String mq) {
        this.mq = mq;
    }

    public String getMqAddress () {
        return mqAddress;
    }

    public void setMqAddress (String mqAddress) {
        this.mqAddress = mqAddress;
    }
}
