package com.kalew515.common.enums;

public enum RpcResponseStatusEnum {

    SUCCESS(200, "The remote call is successful"),

    FAIL(500, "The remote call is fail"),

    FAIL_SAFE(501, "The remote call is fail, fail safe strategy was triggered");

    private final int code;

    private final String message;

    RpcResponseStatusEnum (int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode () {
        return code;
    }

    public String getMessage () {
        return message;
    }

    @Override
    public String toString () {
        return "RpcResponseStatusEnum{" +
                "code=" + code +
                ", message='" + message + '\'' +
                "} " + super.toString();
    }
}