package com.kalew515.exchange.messages;

import com.kalew515.exchange.Message;

import java.io.Serializable;

public class HeartBeatRequest extends Message implements Serializable {

    private final String ping = "ping";

    public HeartBeatRequest () {
    }

    public HeartBeatRequest (Long requestId) {
        super.setRequestId(requestId);
    }

    @Override
    public Integer getMessageType () {
        return HEARTBEAT_TYPE_REQUEST;
    }

    public String getPing () {
        return ping;
    }
}
