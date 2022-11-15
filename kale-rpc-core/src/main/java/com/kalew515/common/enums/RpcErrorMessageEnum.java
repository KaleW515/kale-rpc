package com.kalew515.common.enums;

public enum RpcErrorMessageEnum {

    CLIENT_CONNECT_SERVER_FAILURE("client connect server failed"),
    SERVICE_INVOCATION_FAILURE("Rpc call failed"),
    SERVICE_CAN_NOT_BE_FOUND("There are no services that conform to the rules");

    private final String message;

    RpcErrorMessageEnum (String message) {
        this.message = message;
    }

    public String getMessage () {
        return message;
    }

    @Override
    public String toString () {
        return "RpcErrorMessageEnum{" +
                "message='" + message + '\'' +
                "} " + super.toString();
    }
}