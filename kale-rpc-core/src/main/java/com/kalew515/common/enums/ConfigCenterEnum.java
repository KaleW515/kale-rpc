package com.kalew515.common.enums;

public enum ConfigCenterEnum {

    LOCAL("local");

    // to do
    // APOLLO("apollo")

    private final String name;

    ConfigCenterEnum (String name) {
        this.name = name;
    }

    public String getName () {
        return name;
    }
}
