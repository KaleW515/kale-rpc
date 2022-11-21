package com.kalew515.transport;

import com.kalew515.cluster.LoadBalance;
import com.kalew515.common.enums.CompressEnum;
import com.kalew515.common.enums.SerializerEnum;
import com.kalew515.common.exception.RpcException;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import com.kalew515.utils.CollectionUtil;
import com.kalew515.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

import static com.kalew515.common.enums.RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND;
import static com.kalew515.config.constants.RpcConfigConstants.RPC_LOAD_BALANCER;

public abstract class AbstractRpcClient implements RpcClient {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final RegisterCenter registerCenter;

    protected final LoadBalance loadBalance;

    protected final ConfigCenter configCenter;

    protected SerializerEnum serializer = SerializerEnum.KRYO;

    protected CompressEnum compressType = CompressEnum.GZIP;

    public AbstractRpcClient () {
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.registerCenter = SingletonFactory.getInstance(RegisterCenterImpl.class);
        String loadBalancer = configCenter.getConfig(RPC_LOAD_BALANCER);
        logger.info("loadBalancer is [{}]", loadBalancer);
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class)
                                          .getExtension(loadBalancer);
    }

    public List<String> getServiceAddress (RpcRequest rpcRequest) {
        List<String> serviceUrlList = registerCenter.lookupService(rpcRequest);
        if (CollectionUtil.isEmpty(serviceUrlList)) {
            throw new RpcException(SERVICE_CAN_NOT_BE_FOUND, rpcRequest.getRpcServiceName());
        }
        return serviceUrlList;
    }

    public void setSerializationAndCompress (String serializationName, String compressName) {
        if (!StringUtils.isBlank(serializationName)) {
            for (SerializerEnum value : SerializerEnum.values()) {
                if (value.getName().equals(serializationName)) {
                    this.serializer = value;
                }
            }
        }
        if (!StringUtils.isBlank(compressName)) {
            for (CompressEnum value : CompressEnum.values()) {
                if (value.getName().equals(compressName)) {
                    this.compressType = value;
                }
            }
        }
    }
}
