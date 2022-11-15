package com.kalew515.exchange.impl;

import com.kalew515.exchange.Message;

import java.io.Serializable;

public class HeartBeatResponse extends Message implements Serializable {

    private final String pong = "pong";

    public HeartBeatResponse () {
    }

    public HeartBeatResponse (Integer requestId) {
        super.setRequestId(requestId);
    }

    @Override
    public int getMessageType () {
        return HEARTBEAT_TYPE_RESPONSE;
    }

    public String getPong () {
        return pong;
    }
}
