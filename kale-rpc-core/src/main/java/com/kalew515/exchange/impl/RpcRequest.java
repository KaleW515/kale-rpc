package com.kalew515.exchange.impl;

import com.kalew515.exchange.Message;

import java.io.Serializable;
import java.util.Arrays;

public class RpcRequest extends Message implements Serializable {

    // 调用的接口全限定名
    private String interfaceName;

    // 调用接口的方法名
    private String methodName;

    // 参数
    private Object[] parameters;

    // 参数类型
    private Class<?>[] paramTypes;

    // 版本，为后续不兼容升级提供支持
    private String version;

    // 用于处理一个接口有多个实现类的情况
    private String group;

    public RpcRequest () {
    }

    public RpcRequest (Integer requestId, String interfaceName, String methodName,
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
    public int getMessageType () {
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
