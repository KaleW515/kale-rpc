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

import static com.kalew515.config.constants.RpcConfigConstants.*;

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

            // clear registry path
            ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
            int port = Integer.parseInt(configCenter.getConfig(SERVER_PORT));
            String address = configCenter.getConfig(RPC_REGISTER_CENTER_ADDRESS);
            InetSocketAddress inetSocketAddress =
                    new InetSocketAddress(configCenter.getConfig(SERVER_HOST), port);
            logger.info("do curator clear registry");
            CuratorUtil.clearRegistry(CuratorUtil.getZkClient(address), inetSocketAddress);

            // stop all created thread pool
            try {
                ThreadPoolFactoryUtil.shutDownAllThreadPool();
            } catch (Exception ignored) {

            }

            // stop registered shutdown policy
            shouldShutdown.forEach((key, value) -> {
                logger.info("do {} shutdown", key.toString());
                value.shutdown();
                logger.info("do {} shutdown success", key.toString());
            });
        }));
    }
}
