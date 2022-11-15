package com.kalew515.utils;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

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
                    zkClient.create().creatingParentsIfNeeded()
                            .withMode(CreateMode.EPHEMERAL)
                            .forPath(path);
                } else {
                    String s = zkClient.create().creatingParentsIfNeeded()
                                       .withMode(CreateMode.EPHEMERAL)
                                       .forPath(path, data);
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
        if (SERVICE_ADDRESS_MAP.containsKey(servicePath)) {
            return SERVICE_ADDRESS_MAP.get(servicePath);
        }
        List<String> result = null;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(servicePath, result);
            registerServiceWatcher(servicePath, zkClient);
        } catch (Exception e) {
            logger.error("get children nodes for path [{}] fail", servicePath);
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getDeviceChildrenNodes (CuratorFramework zkClient,
                                                       String deviceName) {
        String devicePath = ZK_REGISTER_ROOT_PATH + DEVICE_PATH + "/" + deviceName;
        if (DEVICE_MAP.containsKey(devicePath)) {
            return DEVICE_MAP.get(devicePath);
        }
        List<String> result = Collections.emptyList();
        try {
            result = zkClient.getChildren().forPath(devicePath);
            SERVICE_ADDRESS_MAP.put(devicePath, result);
            registerDeviceWatcher(devicePath, zkClient);
        } catch (Exception e) {
            logger.error("get children nodes for path [{}] fail", deviceName);
            e.printStackTrace();
        }
        return result;
    }

    public static Integer getDeviceServiceCallTimes (CuratorFramework zkClient,
                                                     String serviceName, String url) {
        String callTimesPath = ZK_REGISTER_ROOT_PATH + SERVICE_PATH + "/" + serviceName + "/" + url;
        byte[] result = null;
        try {
            result = zkClient.getData().forPath(callTimesPath);
            return Integer.parseInt(new String(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
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
        logger.info("All registered services on the server are cleared:[{}]",
                    REGISTERED_PATH_SET);
    }

    public static CuratorFramework getZkClient (String zookeeperAddress) {
        // if zkClient has been started, return directly
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                                          // the server to connect to (can be a server list)
                                          .connectString(zookeeperAddress)
                                          .retryPolicy(retryPolicy)
                                          .build();
        zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    private static void registerServiceWatcher (String rpcServiceName, CuratorFramework zkClient) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, rpcServiceName, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework,
                                                               pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(rpcServiceName);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

    private static void registerDeviceWatcher (String deviceName, CuratorFramework zkClient) throws Exception {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, deviceName, true);
        PathChildrenCacheListener pathChildrenCacheListener = (curatorFramework,
                                                               pathChildrenCacheEvent) -> {
            List<String> deviceList = curatorFramework.getChildren().forPath(deviceName);
            DEVICE_MAP.put(deviceName, deviceList);
        };
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }
}
