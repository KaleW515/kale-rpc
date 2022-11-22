package com.kalew515.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public final class ThreadPoolFactoryUtil {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactoryUtil.class);

    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    public ThreadPoolFactoryUtil () {
    }

    public static ExecutorService createCustomThreadPoolIfAbsent (CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix,
                                                                  k -> createThreadPool(
                                                                          customThreadPoolConfig,
                                                                          threadNamePrefix,
                                                                          daemon));
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(customThreadPoolConfig, threadNamePrefix, daemon);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    public static void shutDownAllThreadPool () throws Exception {
        logger.info("call shutDownAllThreadPool method");
        THREAD_POOLS.forEach((key, executorService) -> {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            } finally {
                logger.info("shut down thread pool [{}] [{}]", key,
                            executorService.isTerminated());
            }
        });
    }

    private static ExecutorService createThreadPool (CustomThreadPoolConfig customThreadPoolConfig, String threadNamePrefix, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(customThreadPoolConfig.getCorePoolSize(),
                                      customThreadPoolConfig.getMaximumPoolSize(),
                                      customThreadPoolConfig.getKeepAliveTime(),
                                      customThreadPoolConfig.getUnit(),
                                      customThreadPoolConfig.getWorkQueue(),
                                      threadFactory);
    }

    public static ThreadFactory createThreadFactory (String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadNamePrefix + "-%d")
                        .setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

}
