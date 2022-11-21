package com.kalew515.remoting.transport;

import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import org.springframework.beans.factory.FactoryBean;

import static com.kalew515.config.constants.RpcConfigConstants.*;

public class RpcClientFactory implements FactoryBean<RpcClient> {

    private static volatile RpcClient rpcClient;

    private final String serializer;

    private final String compress;

    private final String transporter;

    public RpcClientFactory () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.serializer = configCenter.getConfig(RPC_SERIALIZER);
        this.compress = configCenter.getConfig(RPC_COMPRESS);
        this.transporter = configCenter.getConfig(TRANSPORTER);
    }

    public RpcClientFactory (String serializer, String compress, String transporter) {
        this.serializer = serializer;
        this.compress = compress;
        this.transporter = transporter;
    }

    public static RpcClient getRpcClient () {
        if (rpcClient == null) {
            synchronized (RpcClientFactory.class) {
                if (rpcClient == null) {
                    ConfigCenter configCenter = SingletonFactory.getInstance(
                            ConfigCenterImpl.class);
                    String serializer = configCenter.getConfig(RPC_SERIALIZER);
                    String compress = configCenter.getConfig(RPC_COMPRESS);
                    String transporter = configCenter.getConfig(TRANSPORTER);
                    rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class)
                                               .getExtension(transporter);
                    rpcClient.setSerializationAndCompress(serializer, compress);
                }
            }
        }
        return rpcClient;
    }


    @Override
    public RpcClient getObject () throws Exception {
        rpcClient = ExtensionLoader.getExtensionLoader(RpcClient.class)
                                   .getExtension(transporter);
        rpcClient.setSerializationAndCompress(serializer, compress);
        return rpcClient;
    }

    @Override
    public Class<?> getObjectType () {
        return RpcClient.class;
    }
}
