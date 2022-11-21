package com.kalew515.transport;

import com.kalew515.common.enums.RpcServerStateEnum;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.CustomShutdownHook;
import com.kalew515.config.RpcServiceConfig;
import com.kalew515.monitor.MonitorCenter;
import com.kalew515.monitor.MonitorCenterImpl;
import com.kalew515.registry.RegisterCenter;
import com.kalew515.registry.RegisterCenterImpl;
import com.kalew515.utils.NetUtils;
import com.kalew515.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.kalew515.common.enums.RpcServerStateEnum.PREPARED;
import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.DEFAULT_TRANSPORTER_SERVER_PORT;

public abstract class AbstractRpcServer implements RpcServer {

    protected final RegisterCenter registerCenter;
    protected final MonitorCenter monitorCenter;

    protected final ConfigCenter configCenter;
    protected RpcServerStateEnum serverState;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected String host;

    protected Integer port;

    public AbstractRpcServer () {
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.registerCenter = SingletonFactory.getInstance(
                RegisterCenterImpl.class);
        this.monitorCenter = SingletonFactory.getInstance(MonitorCenterImpl.class);
        this.serverState = PREPARED;
    }

    public void registerService (RpcServiceConfig<?> rpcServiceConfig) {
        registerCenter.publishService(rpcServiceConfig);
    }

    @Override
    public void start () {
        if (StringUtils.isBlank(host)) {
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        if (port == null || NetUtils.isInvalidPort(port)) {
            port = DEFAULT_TRANSPORTER_SERVER_PORT;
        }
        start(host, port);
    }

    @Override
    public void start (String host, int port) {
        CustomShutdownHook.getCustomShutdownHook().clearAll();
        doRpcServerStart(host, port);
        this.monitorCenter.reportDeviceRegister(host);
        this.serverState = RpcServerStateEnum.STARTED;
    }

    // For subclasses: do nothing by default.
    protected abstract void doRpcServerStart (String host, int port);

    public RpcServerStateEnum getServerState () {
        return this.serverState;
    }

    public void setServerHostAndPort (String host, Integer port) {
        this.host = host;
        this.port = port;
    }
}
