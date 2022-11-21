package com.kalew515.spring.boot.config.subconfig;

/**
 * @author kale
 * @date 2022/11/21 上午10:45
 */
public class IdGeneratorCenterConfig {

    public String name;

    public String address;

    public IdGeneratorCenterConfig (String name, String address) {
        this.name = name;
        this.address = address;
    }

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
