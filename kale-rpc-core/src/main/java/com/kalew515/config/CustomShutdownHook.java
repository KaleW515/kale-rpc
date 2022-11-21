package com.kalew515.config;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.remoting.zookeeper.CuratorUtil;
import com.kalew515.utils.ThreadPoolFactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_REGISTER_CENTER_ADDRESS;
import static com.kalew515.config.constants.RpcConfigConstants.SERVER_PORT;

public class CustomShutdownHook {

    private static final CustomShutdownHook CUSTOM_SHUTDOWN_HOOK = new CustomShutdownHook();
    private static final Map<Object, ShutdownPolicy> shouldShutdown = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static CustomShutdownHook getCustomShutdownHook () {
        return CUSTOM_SHUTDOWN_HOOK;
    }

    public static void registerShutdown (Object o, ShutdownPolicy shutdownPolicy) {
        shouldShutdown.put(o, shutdownPolicy);
    }

    public void clearAll () {
        logger.info("addShutdownHook for clearAll");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("do clearAll shutdown hook");
            for (Map.Entry<Object, ShutdownPolicy> entry :
                    shouldShutdown.entrySet()) {
                logger.info("do {} shutdown", entry.getKey().toString());
                entry.getValue().shutdown();
            }
            try {
                ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
                int port = Integer.parseInt(configCenter.getConfig(SERVER_PORT));
                String address = configCenter.getConfig(RPC_REGISTER_CENTER_ADDRESS);
                InetSocketAddress inetSocketAddress =
                        new InetSocketAddress(InetAddress.getLocalHost()
                                                         .getHostAddress(), port);
                logger.info("do curator clear registry");
                CuratorUtil.clearRegistry(CuratorUtil.getZkClient(address), inetSocketAddress);
            } catch (UnknownHostException ignored) {
            }
            ThreadPoolFactoryUtil.shutDownAllThreadPool();
        }));
    }
}
