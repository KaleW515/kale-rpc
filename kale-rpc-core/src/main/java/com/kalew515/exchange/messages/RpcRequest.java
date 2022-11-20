package com.kalew515.exchange.messages;

import com.kalew515.exchange.Message;

import java.io.Serializable;
import java.util.Arrays;

public class RpcRequest extends Message implements Serializable {

    private String interfaceName;

    private String methodName;

    private Object[] parameters;

    private Class<?>[] paramTypes;

    private String version;

    private String group;

    public RpcRequest () {
    }

    public RpcRequest (Long requestId, String interfaceName, String methodName,
                       Object[] parameters, Class<?>[] paramTypes, String version, String group) {
        super.setRequestId(requestId);
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.parameters = parameters;
        this.paramTypes = paramTypes;
        this.version = version;
        this.group = group;
    }

    @Override
    public Integer getMessageType () {
        return RPC_MESSAGE_TYPE_REQUEST;
    }

    public String getRpcServiceName () {
        return this.getInterfaceName() + this.getGroup() + this.getVersion();
    }

    public String getInterfaceName () {
        return interfaceName;
    }

    public void setInterfaceName (String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName () {
        return methodName;
    }

    public void setMethodName (String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters () {
        return parameters;
    }

    public void setParameters (Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParamTypes () {
        return paramTypes;
    }

    public void setParamTypes (Class<?>[] paramTypes) {
        this.paramTypes = paramTypes;
    }

    public String getVersion () {
        return version;
    }

    public void setVersion (String version) {
        this.version = version;
    }

    public String getGroup () {
        return group;
    }

    public void setGroup (String group) {
        this.group = group;
    }

    @Override
    public String toString () {
        return "RpcRequest{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", parameters=" + Arrays.toString(parameters) +
                ", paramTypes=" + Arrays.toString(paramTypes) +
                ", version='" + version + '\'' +
                ", group='" + group + '\'' +
                "} " + super.toString();
    }
}
