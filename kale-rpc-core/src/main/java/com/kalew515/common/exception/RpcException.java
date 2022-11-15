package com.kalew515.common.exception;

import com.kalew515.common.enums.RpcErrorMessageEnum;

public class RpcException extends RuntimeException {
    public RpcException (RpcErrorMessageEnum rpcErrorMessageEnum, String detail) {
        super(rpcErrorMessageEnum.getMessage() + ":" + detail);
    }

    public RpcException (String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException (RpcErrorMessageEnum rpcErrorMessageEnum) {
        super(rpcErrorMessageEnum.getMessage());
    }
}
