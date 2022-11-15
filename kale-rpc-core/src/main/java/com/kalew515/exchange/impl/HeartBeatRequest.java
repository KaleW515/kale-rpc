package com.kalew515.exchange.impl;

import com.kalew515.exchange.Message;

import java.io.Serializable;

public class HeartBeatRequest extends Message implements Serializable {

    private final String ping = "ping";

    public HeartBeatRequest () {
    }

    public HeartBeatRequest (Integer requestId) {
        super.setRequestId(requestId);
    }

    @Override
    public int getMessageType () {
        return HEARTBEAT_TYPE_REQUEST;
    }

    public String getPing () {
        return ping;
    }
}
