package com.kalew515.transport;

import com.kalew515.common.enums.CompressEnum;
import com.kalew515.common.enums.SerializerEnum;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import com.kalew515.utils.StringUtils;

import java.net.InetSocketAddress;
import java.util.Set;

public abstract class AbstractRpcClient implements RpcClient {

    public final RegisterCenter registerCenter;

    public SerializerEnum serializationType = SerializerEnum.KRYO;

    public CompressEnum compressType = CompressEnum.GZIP;

    public AbstractRpcClient () {
        this.registerCenter = SingletonFactory.getInstance(RegisterCenterImpl.class);
    }

    public InetSocketAddress getServiceAddress (RpcRequest rpcRequest, Set<String> blackList) {
        return registerCenter.lookupService(rpcRequest, blackList);
    }

    public void setSerializationAndCompress (String serializationName, String compressName) {
        if (!StringUtils.isBlank(serializationName)) {
            for (SerializerEnum value : SerializerEnum.values()) {
                if (value.getName().equals(serializationName)) {
                    this.serializationType = value;
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
