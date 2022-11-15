package com.kalew515.common.mq;

import com.kalew515.common.mq.message.ConnectionMessage;
import com.kalew515.common.mq.message.DeviceRegisterMessage;
import com.kalew515.common.mq.message.DisconnectionMessage;
import com.kalew515.common.mq.message.ReportRpcMessage;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMonitorMessage {

    public static final int CONNECTION_MESSAGE_TYPE = 1;
    public static final int DISCONNECTION_MESSAGE_TYPE = 2;
    public static final int DEVICE_REGISTER_MESSAGE_TYPE = 3;
    public static final int REPORT_RPC_MESSAGE_TYPE = 4;
    private static final Map<Integer, Class<? extends AbstractMonitorMessage>> messageClasses =
            new HashMap<>();

    static {
        messageClasses.put(CONNECTION_MESSAGE_TYPE, ConnectionMessage.class);
        messageClasses.put(DISCONNECTION_MESSAGE_TYPE, DisconnectionMessage.class);
        messageClasses.put(DEVICE_REGISTER_MESSAGE_TYPE, DeviceRegisterMessage.class);
        messageClasses.put(REPORT_RPC_MESSAGE_TYPE, ReportRpcMessage.class);
    }

    private Integer messageType;

    public static Class<? extends AbstractMonitorMessage> getMessageClass (int messageType) {
        return messageClasses.get(messageType);
    }

    public abstract int getMessageType ();

}
