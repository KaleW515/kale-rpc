package com.kalew515.config.constants;

import com.kalew515.utils.NetUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.kalew515.config.constants.defaultconfig.RpcClientDefaultConfig.*;
import static com.kalew515.config.constants.defaultconfig.RpcDefaultConfig.*;
import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.DEFAULT_TRANSPORTER;
import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.*;

public class RpcConfigConstants {

    public static final String TRANSPORTER = "kale-rpc.transporter";
    public static final String SERVER_HOST = "kale-rpc.server.host";
    public static final String SERVER_PORT = "kale-rpc.server.port";
    public static final String MQ = "kale-rpc.server.mq";
    public static final String MQ_ADDRESS = "kale-rpc.server.mq-address";
    public static final String RPC_SERIALIZER = "kale-rpc.client.serializer";
    public static final String RPC_COMPRESS = "kale-rpc.client.compress";
    public static final String RPC_LOAD_BALANCER = "kale-rpc.client.load-balancer";
    public static final String RPC_TIMEOUT = "kale-rpc.client.timeout";
    public static final String FAIL_STRATEGY = "kale-rpc.client.fail-strategy";
    public static final String RPC_REGISTER_CENTER_NAME = "kale-rpc.register-center.name";
    public static final String RPC_REGISTER_CENTER_ADDRESS = "kale-rpc.register-center.address";
    public static final String RPC_MONITOR_CENTER_NAME = "kale-rpc.monitor-center.name";
    public static final String RPC_MONITOR_CENTER_ADDRESS = "kale-rpc.monitor-center.address";
    public static final String RPC_CONFIG_CENTER_NAME = "kale-rpc.config-center.name";
    public static final String RPC_CONFIG_CENTER_USE_REMOTE_CONFIG = "kale-rpc.config-center" +
            ".use-remote-config";
    private static volatile Map<String, String> keys;

    public static Map<String, String> getConfigMap () {
        if (keys == null || keys.size() == 0) {
            synchronized (RpcConfigConstants.class) {
                if (keys == null || keys.size() == 0) {
                    keys = new ConcurrentHashMap<>(16);
                    keys.put(SERVER_HOST, NetUtils.getLocalHost());
                    keys.put(SERVER_PORT, DEFAULT_TRANSPORTER_SERVER_PORT.toString());
                    keys.put(TRANSPORTER, DEFAULT_TRANSPORTER);
                    keys.put(MQ, DEFAULT_MQ);
                    keys.put(MQ_ADDRESS, DEFAULT_MQ_ADDRESS);
                    keys.put(RPC_REGISTER_CENTER_NAME, DEFAULT_REGISTER_CENTER);
                    keys.put(RPC_REGISTER_CENTER_ADDRESS, DEFAULT_ZK_ADDRESS);
                    keys.put(RPC_MONITOR_CENTER_NAME, DEFAULT_MONITOR_CENTER);
                    keys.put(RPC_MONITOR_CENTER_ADDRESS, DEFAULT_ZK_ADDRESS);
                    keys.put(RPC_CONFIG_CENTER_NAME, DEFAULT_CONFIG_CENTER);
                    keys.put(RPC_CONFIG_CENTER_USE_REMOTE_CONFIG, DEFAULT_USE_REMOTE_CONFIG);
                    keys.put(RPC_SERIALIZER, DEFAULT_SERIALIZATION);
                    keys.put(RPC_COMPRESS, DEFAULT_COMPRESS);
                    keys.put(RPC_LOAD_BALANCER, DEFAULT_LOAD_BALANCER);
                    keys.put(RPC_TIMEOUT, String.valueOf(DEFAULT_TIMEOUT));
                    keys.put(FAIL_STRATEGY, DEFAULT_FAIL_STRATEGY);
                }
            }
        }
        return keys;
    }

}
