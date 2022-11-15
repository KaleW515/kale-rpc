package com.kalew515.exchange;

import com.kalew515.exchange.impl.RpcRequest;
import com.kalew515.exchange.impl.RpcResponse;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Message implements Serializable {

    public static final int RPC_MESSAGE_TYPE_REQUEST = 1;
    public static final int RPC_MESSAGE_TYPE_RESPONSE = 2;
    public static final int HEARTBEAT_TYPE_REQUEST = 3;
    public static final int HEARTBEAT_TYPE_RESPONSE = 4;
    private static final Map<Integer, Class<? extends Message>> messageClasses = new HashMap<>();

    static {
        messageClasses.put(RPC_MESSAGE_TYPE_REQUEST, RpcRequest.class);
        messageClasses.put(RPC_MESSAGE_TYPE_RESPONSE, RpcResponse.class);
        messageClasses.put(HEARTBEAT_TYPE_REQUEST, RpcResponse.class);
        messageClasses.put(HEARTBEAT_TYPE_RESPONSE, RpcResponse.class);
    }

    // 请求id
    private int requestId;
    // 消息类型
    private int messageType;
    // 序列化类型
    private byte codec;
    // 压缩类型
    private byte compress;

    public static Class<? extends Message> getMessageClass (int messageType) {
        return messageClasses.get(messageType);
    }

    public abstract int getMessageType ();

    public void setMessageType (int messageType) {
        this.messageType = messageType;
    }

    public int getRequestId () {
        return requestId;
    }

    public void setRequestId (int requestId) {
        this.requestId = requestId;
    }

    public byte getCodec () {
        return codec;
    }

    public void setCodec (byte codec) {
        this.codec = codec;
    }

    public byte getCompress () {
        return compress;
    }

    public void setCompress (byte compress) {
        this.compress = compress;
    }

    @Override
    public String toString () {
        return "Message{" +
                "requestId=" + requestId +
                ", messageType=" + messageType +
                ", codec=" + codec +
                ", compress=" + compress +
                '}';
    }
}
