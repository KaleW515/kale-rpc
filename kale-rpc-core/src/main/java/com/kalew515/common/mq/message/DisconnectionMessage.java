package com.kalew515.common.mq.message;

import com.kalew515.common.mq.AbstractMonitorMessage;

public class DisconnectionMessage extends AbstractMonitorMessage {

    private String localHost;

    private String remoteHost;

    public DisconnectionMessage (String localHost, String remoteHost) {
        this.localHost = localHost;
        this.remoteHost = remoteHost;
    }

    @Override
    public int getMessageType () {
        return DISCONNECTION_MESSAGE_TYPE;
    }

    public String getLocalHost () {
        return localHost;
    }

    public void setLocalHost (String localHost) {
        this.localHost = localHost;
    }

    public String getRemoteHost () {
        return remoteHost;
    }

    public void setRemoteHost (String remoteHost) {
        this.remoteHost = remoteHost;
    }
}
