package com.kalew515.monitor.center.zk;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.common.mq.MessageCenterImpl;
import com.kalew515.common.mq.message.ConnectionMessage;
import com.kalew515.common.mq.message.DeviceRegisterMessage;
import com.kalew515.common.mq.message.DisconnectionMessage;
import com.kalew515.common.mq.message.ReportRpcMessage;
import com.kalew515.config.CustomShutdownHook;
import com.kalew515.monitor.MonitorReport;
import com.kalew515.utils.CustomThreadPoolConfig;
import com.kalew515.utils.ThreadPoolFactoryUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ZkMonitorReportImpl implements MonitorReport {

    private static final String threadName = "zk-report-thread";
    private final MessageCenterImpl messageCenter;
    private ExecutorService reportThreadPool;

    public ZkMonitorReportImpl () {
        messageCenter = SingletonFactory.getInstance(MessageCenterImpl.class);
        reportThreadPoolInit();
        reportThreadPool.submit(new ZkReportRunnable());
        // register shutdown hook
        CustomShutdownHook.registerShutdown(ZkReportRunnable.class,
                                            ZkReportRunnable::shutdownGracefully);
    }

    private void reportThreadPoolInit () {
        CustomThreadPoolConfig poolConfig = new CustomThreadPoolConfig();
        poolConfig.setCorePoolSize(1);
        poolConfig.setMaximumPoolSize(1);
        poolConfig.setKeepAliveTime(0L);
        poolConfig.setUnit(TimeUnit.MILLISECONDS);
        poolConfig.setWorkQueue(new LinkedBlockingQueue<Runnable>());
        reportThreadPool = ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent(
                poolConfig, threadName, false);
    }

    @Override
    public void reportConn (String localHost, String remoteHost) {
        ConnectionMessage connectionMessage = new ConnectionMessage(localHost, remoteHost);
        boolean isSuccess = false;
        while (!isSuccess) {
            isSuccess = messageCenter.putMessage(connectionMessage);
        }
    }

    @Override
    public void reportDeviceRegister (String host) {
        DeviceRegisterMessage deviceRegisterMessage = new DeviceRegisterMessage(host);
        boolean isSuccess = false;
        while (!isSuccess) {
            isSuccess = messageCenter.putMessage(deviceRegisterMessage);
        }
    }

    @Override
    public void reportDisconn (String localHost, String remoteHost) {
        DisconnectionMessage disconnectionMessage = new DisconnectionMessage(localHost, remoteHost);
        boolean isSuccess = false;
        while (!isSuccess) {
            isSuccess = messageCenter.putMessage(disconnectionMessage);
        }
    }

    @Override
    public void reportRpc (String serviceName, String address) {
        ReportRpcMessage reportRpcMessage = new ReportRpcMessage(serviceName, address);
        boolean isSuccess = false;
        while (!isSuccess) {
            isSuccess = messageCenter.putMessage(reportRpcMessage);
        }
    }
}
