package com.kalew515.remoting.zookeeper;

import com.kalew515.config.CustomShutdownHook;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class CuratorUtil {

    public static final String ZK_REGISTER_ROOT_PATH = "/kale-rpc";
    public static final String SERVICE_PATH = "/service";
    public static final String DEVICE_PATH = "/device";
    private static final Logger logger = LoggerFactory.getLogger(CuratorUtil.class);
    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();

    private static final Map<String, List<String>> DEVICE_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();

    private static final Map<String, AtomicInteger> CACHED_SERVICE_CALL_TIMES =
            new ConcurrentHashMap<>();

    private static CuratorFramework zkClient;

    private CuratorUtil () {
    }

    public static void createPersistentNode (CuratorFramework zkClient, String path) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists()
                                                              .forPath(path) != null) {
                logger.info("The node already exists. The node is:[{}]", path);
            } else {
                zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT)
                        .forPath(path);
                logger.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
        }
    }

    public static void createEphemeralNode (CuratorFramework zkClient, String path, byte[] data) {
        try {
            if (REGISTERED_PATH_SET.contains(path) || zkClient.checkExists()
                                                              .forPath(path) != null) {
                logger.info("The node already exists. The node is:[{}]", path);
            } else {
                if (data == null || data.length == 0) {
                    zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL)
                            .forPath(path);
                } else {
                    String s = zkClient.create().creatingParentsIfNeeded()
                                       .withMode(CreateMode.EPHEMERAL).forPath(path, data);
                    zkClient.setData().forPath(s, data);
                }
                logger.info("The node was created successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.add(path);
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
            e.printStackTrace();
        }
    }

    public static void deleteNode (CuratorFramework zkClient, String path) {
        try {
            if (!REGISTERED_PATH_SET.contains(path) && zkClient.checkExists()
                                                               .forPath(path) == null) {
                logger.info("The node is not exists. The node is:[{}]", path);
            } else {
                zkClient.delete().deletingChildrenIfNeeded().forPath(path);
                logger.info("The node was deleted successfully. The node is:[{}]", path);
            }
            REGISTERED_PATH_SET.remove(path);
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
            e.printStackTrace();
        }
    }

    public static void setRpcTimes (CuratorFramework zkClient, String path) {
        try {
            byte[] bytes = zkClient.getData().forPath(path);
            int count = Integer.parseInt(new String(bytes));
            zkClient.setData().withVersion(-1).forPath(path, String.valueOf(count + 1).getBytes());
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
            e.printStackTrace();
        }
    }

    public static List<String> getServiceChildrenNodes (CuratorFramework zkClient,
                                                        String rpcServiceName) {
        String servicePath = ZK_REGISTER_ROOT_PATH + SERVICE_PATH + "/" + rpcServiceName;
        if (!SERVICE_ADDRESS_MAP.containsKey(servicePath)) {
            synchronized (servicePath.intern()) {
                if (!SERVICE_ADDRESS_MAP.containsKey(servicePath)) {
                    List<String> result = null;
                    try {
                        result = zkClient.getChildren().forPath(servicePath);
                        SERVICE_ADDRESS_MAP.put(servicePath, result);
                        registerDeviceWatcher(servicePath, zkClient);
                    } catch (Exception e) {
                        logger.error("get children nodes for path [{}] fail", servicePath);
                        e.printStackTrace();
                    }
                }
            }
        }
        return SERVICE_ADDRESS_MAP.get(servicePath);
    }

    public static List<String> getDeviceChildrenNodes (CuratorFramework zkClient,
                                                       String deviceName) {
        String devicePath = ZK_REGISTER_ROOT_PATH + DEVICE_PATH + "/" + deviceName;
        if (!DEVICE_MAP.containsKey(devicePath)) {
            synchronized (devicePath.intern()) {
                if (!DEVICE_MAP.containsKey(devicePath)) {
                    List<String> result = null;
                    try {
                        result = zkClient.getChildren().forPath(devicePath);
                        DEVICE_MAP.put(deviceName, result);
                        registerServiceWatcher(deviceName, zkClient);
                    } catch (Exception e) {
                        logger.error("get children nodes for path [{}] fail", deviceName);
                        e.printStackTrace();
                    }
                }
            }
        }
        return DEVICE_MAP.get(devicePath);
    }

    public static Integer getServiceCallTimes (CuratorFramework zkClient,
                                               String serviceName, String url) {
        String callTimesPath = ZK_REGISTER_ROOT_PATH + SERVICE_PATH + "/" + serviceName + "/" + url;
        if (!CACHED_SERVICE_CALL_TIMES.containsKey(callTimesPath)) {
            synchronized (callTimesPath.intern()) {
                if (!CACHED_SERVICE_CALL_TIMES.containsKey(callTimesPath)) {
                    byte[] result = null;
                    try {
                        result = zkClient.getData().forPath(callTimesPath);
                        int times = Integer.parseInt(new String(result));
                        CACHED_SERVICE_CALL_TIMES.put(callTimesPath, new AtomicInteger(times));
                        registerServiceCallTimesWatcher(callTimesPath, zkClient);
                        return times;
                    } catch (Exception e) {
                        e.printStackTrace();
                        CACHED_SERVICE_CALL_TIMES.put(callTimesPath, new AtomicInteger());
                    }
                }
            }
        }
        return CACHED_SERVICE_CALL_TIMES.get(callTimesPath).get();
    }

    public static void clearRegistry (CuratorFramework zkClient,
                                      InetSocketAddress inetSocketAddress) {
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            try {
                if (p.endsWith(inetSocketAddress.toString()) || p.endsWith(
                        inetSocketAddress.getAddress().getHostAddress())) {
                    zkClient.delete().deletingChildrenIfNeeded().forPath(p);
                }
            } catch (Exception e) {
                logger.error("clear registry for path [{}] fail", p);
            }
        });
        logger.info("All registered services on the server are cleared:[{}]", REGISTERED_PATH_SET);
    }

    public static CuratorFramework getZkClient (String zookeeperAddress) {
        if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
            synchronized (CuratorUtil.class) {
                if (zkClient == null || zkClient.getState() != CuratorFrameworkState.STARTED) {
                    RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME,
                                                                          MAX_RETRIES);
                    zkClient = CuratorFrameworkFactory.builder()
                                                      .connectString(zookeeperAddress)
                                                      .retryPolicy(retryPolicy)
                                                      .build();
                    zkClient.start();
                    try {
                        if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                            throw new RuntimeException("Time out waiting to connect to ZK!");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return zkClient;
    }

    private static void registerServiceWatcher (String rpcServiceName, CuratorFramework zkClient) {
        CuratorCache curatorCache = CuratorCache.builder(zkClient, rpcServiceName).build();
        CuratorCacheListener listener = CuratorCacheListener.builder().forNodeCache(() -> {
            List<String> serviceAddresses = zkClient.getChildren()
                                                    .forPath(rpcServiceName);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        }).build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
        CustomShutdownHook.registerShutdown(curatorCache, curatorCache::close);
    }

    private static void registerDeviceWatcher (String deviceName, CuratorFramework zkClient) {
        CuratorCache curatorCache = CuratorCache.builder(zkClient, deviceName).build();
        CuratorCacheListener listener = CuratorCacheListener.builder().forNodeCache(() -> {
            List<String> serviceAddresses = zkClient.getChildren()
                                                    .forPath(deviceName);
            DEVICE_MAP.put(deviceName, serviceAddresses);
        }).build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
        CustomShutdownHook.registerShutdown(curatorCache, curatorCache::close);
    }

    private static void registerServiceCallTimesWatcher (String callTimesPath,
                                                         CuratorFramework zkClient) {
        CuratorCache curatorCache = CuratorCache.builder(zkClient, callTimesPath).build();
        CuratorCacheListener listener = CuratorCacheListener.builder().forNodeCache(() -> {
            CACHED_SERVICE_CALL_TIMES.get(callTimesPath).incrementAndGet();
        }).build();
        curatorCache.listenable().addListener(listener);
        curatorCache.start();
        CustomShutdownHook.registerShutdown(curatorCache, curatorCache::close);
    }
}
