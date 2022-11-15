package com.kalew515.common.mq.message;

import com.kalew515.common.mq.AbstractMonitorMessage;

public class DeviceRegisterMessage extends AbstractMonitorMessage {

    private String host;

    public DeviceRegisterMessage (String host) {
        this.host = host;
    }

    @Override
    public int getMessageType () {
        return DEVICE_REGISTER_MESSAGE_TYPE;
    }

    public String getHost () {
        return host;
    }

    public void setHost (String host) {
        this.host = host;
    }
}
