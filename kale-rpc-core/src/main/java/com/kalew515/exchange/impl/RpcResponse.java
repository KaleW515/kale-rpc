package com.kalew515.exchange.impl;

import com.kalew515.common.enums.RpcResponseStatusEnum;
import com.kalew515.exchange.Message;

import java.io.Serializable;

public class RpcResponse<T> extends Message implements Serializable {

    // response message
    private String message;

    // response code
    private Integer code;

    // body
    private T data;

    public RpcResponse () {
    }

    public RpcResponse (Integer requestId, String message, Integer code, T data) {
        super.setRequestId(requestId);
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> RpcResponse<T> success (T data, int requestId) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(RpcResponseStatusEnum.SUCCESS.getCode());
        rpcResponse.setMessage(RpcResponseStatusEnum.SUCCESS.getMessage());
        rpcResponse.setRequestId(requestId);
        if (data != null) {
            rpcResponse.setData(data);
        }
        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail (RpcResponseStatusEnum rpcResponseStatusEnum) {
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(rpcResponse.getCode());
        rpcResponse.setMessage(rpcResponse.getMessage());
        return rpcResponse;
    }

    @Override
    public int getMessageType () {
        return RPC_MESSAGE_TYPE_RESPONSE;
    }

    public String getMessage () {
        return message;
    }

    public void setMessage (String message) {
        this.message = message;
    }

    public Integer getCode () {
        return code;
    }

    public void setCode (Integer code) {
        this.code = code;
    }

    public T getData () {
        return data;
    }

    public void setData (T data) {
        this.data = data;
    }
}
