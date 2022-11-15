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

    public static void shutDownAllThreadPool () {
        logger.info("call shutDownAllThreadPool method");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("shut down thread pool [{}] [{}]", entry.getKey(),
                        executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                logger.error("Thread pool never terminated");
                executorService.shutdownNow();
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
