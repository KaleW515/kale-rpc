package com.kalew515.proxy;

import com.kalew515.cluster.FailStrategy;
import com.kalew515.cluster.LoadBalance;
import com.kalew515.common.enums.RpcErrorMessageEnum;
import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.exchange.IdGeneratorCenter;
import com.kalew515.exchange.IdGeneratorCenterImpl;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.cluster.context.RequestContext;
import com.kalew515.remoting.transport.RpcClient;
import com.kalew515.utils.NetUtils;
import com.kalew515.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND;
import static com.kalew515.config.constants.RpcConfigConstants.*;

public class RpcClientProxy implements InvocationHandler {

    private static final String INTERFACE_NAME = "interfaceName";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RpcClient transporter;

    private final RpcServiceConfig<?> rpcServiceConfig;

    private final ConfigCenter configCenter;

    private final FailStrategy failStrategy;

    private final IdGeneratorCenter idGeneratorCenter;

    private final LoadBalance loadBalance;

    private final Integer timeout;

    public RpcClientProxy (RpcClient transporter, RpcServiceConfig<?> rpcServiceConfig) {
        this.transporter = transporter;
        this.rpcServiceConfig = rpcServiceConfig;
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.idGeneratorCenter = SingletonFactory.getInstance(IdGeneratorCenterImpl.class);
        this.timeout = Integer.parseInt(configCenter.getConfig(RPC_TIMEOUT));
        this.failStrategy = ExtensionLoader.getExtensionLoader(FailStrategy.class)
                                           .getExtension(configCenter.getConfig(FAIL_STRATEGY));
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                                          .getExtension(configCenter.getConfig(RPC_LOAD_BALANCER));
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
        logger.debug("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = new RpcRequest(idGeneratorCenter.generateId(),
                                               method.getDeclaringClass().getName(),
                                               method.getName(), args, method.getParameterTypes(),
                                               rpcServiceConfig.getVersion(),
                                               rpcServiceConfig.getGroup());
        RpcResponse<?> rpcResponse = null;
        List<String> serviceUrlList = transporter.getServiceAddress(
                rpcRequest);
        String rpcServiceName = rpcRequest.getRpcServiceName();
        // load balancing
        String targetServiceUrl;
        logger.debug("loadBalancer is " + loadBalance.getClass().getCanonicalName());
        targetServiceUrl = loadBalance.selectServiceAddress(serviceUrlList,
                                                            rpcRequest);
        logger.debug("Successfully found the service address: [{}]", targetServiceUrl);
        if (StringUtils.isBlank(targetServiceUrl)) {
            throw new RpcException(SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        // send rpc
        InetSocketAddress inetSocketAddress = NetUtils.stringToInetSocketAddress(targetServiceUrl);
        CompletableFuture<RpcResponse<?>> completableFuture =
                transporter.sendRpcRequest(
                        rpcRequest, inetSocketAddress);
        try {
            rpcResponse = completableFuture.get(timeout, TimeUnit.MILLISECONDS);
        } catch (TimeoutException | ExecutionException e) {
            logger.debug("request failed, fail strategy triggered");
            rpcResponse = this.failStrategy.strategy(
                    new RequestContext(transporter, rpcRequest,
                                       inetSocketAddress,
                                       timeout,
                                       TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            logger.warn(e.getLocalizedMessage());
        }
        check(rpcResponse, rpcRequest);
        return rpcResponse.getData();
    }
}
