package com.kalew515.exchange.messages;

import com.kalew515.common.enums.RpcResponseStatusEnum;
import com.kalew515.exchange.Message;

import java.io.Serializable;

public class RpcResponse<T> extends Message implements Serializable {

    private String message;

    private Integer code;

    private T data;

    public RpcResponse () {
    }

    public RpcResponse (Long requestId, String message, Integer code, T data) {
        super.setRequestId(requestId);
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> RpcResponse<T> success (T data, Long requestId) {
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
    public Integer getMessageType () {
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
