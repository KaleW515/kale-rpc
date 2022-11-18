package com.kalew515.spring.boot.config.subconfig;

public class ConfigCenterConfig {

    public String name;

    public String address;

    public Boolean useRemoteConfig;

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }
}
