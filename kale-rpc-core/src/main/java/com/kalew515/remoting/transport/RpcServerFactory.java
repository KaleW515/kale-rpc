package com.kalew515.remoting.transport;

import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import org.springframework.beans.factory.FactoryBean;

import static com.kalew515.config.constants.RpcConfigConstants.*;

public class RpcServerFactory implements FactoryBean<RpcServer> {

    private static volatile RpcServer rpcServer;


    private final String hostToBind;

    private final Integer portToBind;

    private final String transporter;

    public RpcServerFactory () {
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        transporter = configCenter.getConfig(TRANSPORTER);
        hostToBind = configCenter.getConfig(SERVER_HOST);
        portToBind = Integer.parseInt(configCenter.getConfig(SERVER_PORT));
    }

    public RpcServerFactory (String host, Integer port, String transporter) {
        this.transporter = transporter;
        this.hostToBind = host;
        this.portToBind = port;
    }

    public static RpcServer getRpcServer () {
        if (rpcServer == null) {
            synchronized (RpcServerFactory.class) {
                if (rpcServer == null) {
                    ConfigCenter configCenter = SingletonFactory.getInstance(
                            ConfigCenterImpl.class);
                    String transporter = configCenter.getConfig(TRANSPORTER);
                    rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class)
                                               .getExtension(transporter);
                    String host = configCenter.getConfig(SERVER_HOST);
                    Integer port = Integer.parseInt(configCenter.getConfig(SERVER_PORT));
                    rpcServer.setServerHostAndPort(host, port);
                }
            }
        }
        return rpcServer;
    }

    @Override
    public RpcServer getObject () throws Exception {
        rpcServer = ExtensionLoader.getExtensionLoader(RpcServer.class)
                                   .getExtension(transporter);
        rpcServer.setServerHostAndPort(hostToBind, portToBind);
        return rpcServer;
    }

    @Override
    public Class<?> getObjectType () {
        return RpcServer.class;
    }
}
