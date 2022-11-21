package com.kalew515.cluster.failstrategy;

import com.kalew515.cluster.LoadBalance;
import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.constants.RpcConfigConstants;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.cluster.FailStrategy;
import com.kalew515.proxy.RpcClientProxy;
import com.kalew515.cluster.context.RequestContext;
import com.kalew515.remoting.transport.RpcClient;
import com.kalew515.utils.NetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE;

public class FailOver implements FailStrategy {

    private final int retryTimes = 1;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LoadBalance loadBalance;

    public FailOver () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String loadBalancer = configCenter.getConfig(RpcConfigConstants.RPC_LOAD_BALANCER);
        loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                                     .getExtension(loadBalancer);
    }

    @Override
    public RpcResponse<?> strategy (RequestContext requestContext) {
        RpcClient rpcClient = requestContext.getRpcClient();
        InetSocketAddress lastAddress = requestContext.getInetSocketAddress();
        List<String> serviceUrlList = rpcClient.getServiceAddress(requestContext.getRpcRequest());

        for (int i = 0; i < retryTimes; i++) {
            removeBadUrl(serviceUrlList, lastAddress);
            if (serviceUrlList.size() == 0) {
                throw new RpcException(SERVICE_INVOCATION_FAILURE);
            }
            String url = loadBalance.selectServiceAddress(serviceUrlList,
                                                          requestContext.getRpcRequest());
            InetSocketAddress inetSocketAddress = NetUtils.stringToInetSocketAddress(url);
            CompletableFuture<RpcResponse<?>> rpcResponse = rpcClient.sendRpcRequest(
                    requestContext.getRpcRequest(), inetSocketAddress);
            try {
                RpcResponse<?> response = rpcResponse.get(requestContext.getTimeout(),
                                                          requestContext.getTimeUnit());
                RpcClientProxy.check(response, requestContext.getRpcRequest());
                return response;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException | TimeoutException e) {
                logger.warn(e.getMessage());
                lastAddress = inetSocketAddress;
            }
        }
        throw new RpcException(SERVICE_INVOCATION_FAILURE);
    }

    private void removeBadUrl (List<String> serviceUrlList, InetSocketAddress badAddress) {
        Iterator<String> iterator = serviceUrlList.iterator();
        while (iterator.hasNext()) {
            if (NetUtils.inetSocketAddressIsEqualsString(badAddress, iterator.next())) {
                iterator.remove();
                break;
            }
        }
    }
}
