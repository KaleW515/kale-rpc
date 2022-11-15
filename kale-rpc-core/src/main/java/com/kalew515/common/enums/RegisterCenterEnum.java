package com.kalew515.common.enums;

public enum RegisterCenterEnum {

    ZK("zk");

    private final String name;

    RegisterCenterEnum (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }
}
