package com.kalew515.common.enums;

public enum CompressEnum {

    GZIP((byte) 0x01, "gzip"),

    DONT((byte) 0x02, "dont");

    private final byte code;

    private final String name;

    CompressEnum (byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName (byte code) {
        for (CompressEnum c : CompressEnum.values()) {
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
