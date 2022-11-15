package com.kalew515.common.mq.message;

import com.kalew515.common.mq.AbstractMonitorMessage;

public class ReportRpcMessage extends AbstractMonitorMessage {

    private String serviceName;

    private String address;

    public ReportRpcMessage (String serviceName, String address) {
        this.serviceName = serviceName;
        this.address = address;
    }

    @Override
    public int getMessageType () {
        return REPORT_RPC_MESSAGE_TYPE;
    }

    public String getServiceName () {
        return serviceName;
    }

    public void setServiceName (String serviceName) {
        this.serviceName = serviceName;
    }

    public String getAddress () {
        return address;
    }

    public void setAddress (String address) {
        this.address = address;
    }
}
