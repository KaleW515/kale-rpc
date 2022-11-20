package com.kalew515.exchange;

import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Message implements Serializable {

    public static final Integer RPC_MESSAGE_TYPE_REQUEST = 1;
    public static final Integer RPC_MESSAGE_TYPE_RESPONSE = 2;
    public static final Integer HEARTBEAT_TYPE_REQUEST = 3;
    public static final Integer HEARTBEAT_TYPE_RESPONSE = 4;
    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequest.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponse.class);
        messageClasses.put(HEARTBEAT_TYPE_REQUEST, RpcResponse.class);
        messageClasses.put(HEARTBEAT_TYPE_RESPONSE, RpcResponse.class);
    }

    private Long requestId;
    private Integer messageType;
    private Byte serializer;
    private Byte compress;

    public static Class<? extends Message> getMessageClass (int messageType) {
        return messageClasses.get(messageType);
    }

    public abstract Integer getMessageType ();

    public void setMessageType (Integer messageType) {
        this.messageType = messageType;
    }

    public Long getRequestId () {
        return requestId;
    }

    public void setRequestId (Long requestId) {
        this.requestId = requestId;
    }

    public Byte getSerializer () {
        return serializer;
    }

    public void setSerializer (Byte serializer) {
        this.serializer = serializer;
    }

    public Byte getCompress () {
        return compress;
    }

    public void setCompress (Byte compress) {
        this.compress = compress;
    }

    @Override
    public String toString () {
        return "Message{" +
                "requestId=" + requestId +
                ", messageType=" + messageType +
                ", codec=" + serializer +
                ", compress=" + compress +
                '}';
    }
}
