package com.kalew515.common.enums;

public enum TransporterEnum {

    NETTY("netty");

    private final String name;

    TransporterEnum (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }
}
