package com.kalew515.common.enums;

public enum SerializerEnum {
    KRYO((byte) 0x01, "kryo"),

    JSON((byte) 0X02, "json");

    private final byte code;

    private final String name;

    SerializerEnum (byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName (byte code) {
        for (SerializerEnum c : SerializerEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

    public byte getCode () {
        return code;
    }

    public String getName () {
        return name;
    }
}
