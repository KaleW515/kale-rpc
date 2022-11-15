package com.kalew515.proxy;

import com.kalew515.common.enums.RpcErrorMessageEnum;
import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.exchange.impl.RpcRequest;
import com.kalew515.exchange.impl.RpcResponse;
import com.kalew515.proxy.context.RequestContext;
import com.kalew515.transport.RpcClient;
import com.kalew515.transport.netty.client.NettyRpcClient;
import com.kalew515.utils.RequestIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.kalew515.config.constants.RpcConfigConstants.FAIL_STRATEGY;
import static com.kalew515.config.constants.RpcConfigConstants.RPC_TIMEOUT;

public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RpcClient rpcRequestTransport;

    private final RpcServiceConfig<?> rpcServiceConfig;

    private final ConfigCenter configCenter;

    private final FailStrategy failStrategy;

    private final Integer timeout;

    public RpcClientProxy (RpcClient rpcRequestTransport, RpcServiceConfig<?> rpcServiceConfig) {
        this.rpcRequestTransport = rpcRequestTransport;
        this.rpcServiceConfig = rpcServiceConfig;
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.timeout = Integer.parseInt(configCenter.getConfig(RPC_TIMEOUT));
        this.failStrategy = ExtensionLoader.getExtensionLoader(FailStrategy.class)
                                           .getExtension(configCenter.getConfig(FAIL_STRATEGY));
    }

    public static void check (RpcResponse<?> rpcResponse, RpcRequest rpcRequest) {
        if (rpcResponse == null) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE,
                                   INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }

    public <T> T getProxy (Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    @Override
    public Object invoke (Object proxy, Method method, Object[] args) {
        logger.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = new RpcRequest(RequestIdGenerator.getRequestId(),
                                               method.getDeclaringClass().getName(),
                                               method.getName(), args, method.getParameterTypes(),
                                               rpcServiceConfig.getVersion(),
                                               rpcServiceConfig.getGroup());
        RpcResponse<?> rpcResponse = null;
        InetSocketAddress inetSocketAddress = rpcRequestTransport.getServiceAddress(
                rpcRequest, Collections.emptySet());
        if (rpcRequestTransport instanceof NettyRpcClient) {
            CompletableFuture<RpcResponse<?>> completableFuture =
                    rpcRequestTransport.sendRpcRequest(
                            rpcRequest, inetSocketAddress);
            try {
                rpcResponse = completableFuture.get(timeout, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | ExecutionException e) {
                logger.info("request fail, fail strategy triggered");
                rpcResponse = this.failStrategy.strategy(
                        new RequestContext(rpcRequestTransport, rpcRequest,
                                           inetSocketAddress,
                                           timeout,
                                           TimeUnit.MILLISECONDS));
            } catch (InterruptedException e) {
                logger.warn(e.getLocalizedMessage());
            }
        }
        check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }
}
