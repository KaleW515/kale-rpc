package com.kalew515.remoting.transport.netty.handler;

import com.kalew515.common.exception.RpcException;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RpcRequestHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RegisterCenter registerCenter;

    public RpcRequestHandler () {
        registerCenter = SingletonFactory.getInstance(RegisterCenterImpl.class);
    }

    public Object handle (RpcRequest rpcRequest) {
        Object service = registerCenter.getService(rpcRequest.getRpcServiceName());
        return invokeTargetMethod(rpcRequest, service);
    }

    private Object invokeTargetMethod (RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),
                                                         rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.debug("service:[{}] successful invoke method:[{}]", rpcRequest.getInterfaceName(),
                        rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }
}
