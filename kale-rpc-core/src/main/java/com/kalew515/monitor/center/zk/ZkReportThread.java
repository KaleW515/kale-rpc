package com.kalew515.monitor.center.zk;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.common.mq.AbstractMonitorMessage;
import com.kalew515.common.mq.MessageCenter;
import com.kalew515.common.mq.MessageCenterImpl;
import com.kalew515.common.mq.message.ConnectionMessage;
import com.kalew515.common.mq.message.DeviceRegisterMessage;
import com.kalew515.common.mq.message.DisconnectionMessage;
import com.kalew515.common.mq.message.ReportRpcMessage;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.remoting.zookeeper.CuratorUtil;
import org.apache.curator.framework.CuratorFramework;

import static com.kalew515.config.constants.RpcConfigConstants.RPC_MONITOR_CENTER_ADDRESS;

public class ZkReportThread extends Thread {

    private static final String threadName = "zk-report-thread";
    private static boolean flag = true;
    private final MessageCenter messageCenter;

    private final String monitorCenterAddress;

    public ZkReportThread () {
        super(threadName);
        ConfigCenter configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        monitorCenterAddress = configCenter.getConfig(RPC_MONITOR_CENTER_ADDRESS);
        this.messageCenter = SingletonFactory.getInstance(MessageCenterImpl.class);
    }

    public static void shutdownGracefully () {
        ZkReportThread.flag = false;
    }

    @Override
    public void run () {
        while (flag) {
            AbstractMonitorMessage abstractMonitorMessage = this.messageCenter.takeMessage();
            int messageType = abstractMonitorMessage.getMessageType();
            switch (messageType) {
                case 1: // connection message
                    connectionMessageDeal(abstractMonitorMessage);
                    break;
                case 2: // disconnection message
                    disconnectionMessageDeal(abstractMonitorMessage);
                    break;
                case 3: // register device message
                    registerDeviceMessageDeal(abstractMonitorMessage);
                    break;
                case 4: // report rpc message
                    reportRpcMessageDeal(abstractMonitorMessage);
                    break;
                default:
                    break;
            }
        }
    }

    private void connectionMessageDeal (AbstractMonitorMessage message) {
        ConnectionMessage connectionMessage = (ConnectionMessage) message;
        String localHost = connectionMessage.getLocalHost();
        String remoteHost = connectionMessage.getRemoteHost();
        String connectPath =
                CuratorUtil.ZK_REGISTER_ROOT_PATH + CuratorUtil.DEVICE_PATH + localHost + remoteHost;
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        CuratorUtil.createEphemeralNode(zkClient, connectPath, null);
    }

    private void disconnectionMessageDeal (AbstractMonitorMessage message) {
        DisconnectionMessage disconnectionMessage = (DisconnectionMessage) message;
        String localHost = disconnectionMessage.getLocalHost();
        String remoteHost = disconnectionMessage.getRemoteHost();
        String connectPath =
                CuratorUtil.ZK_REGISTER_ROOT_PATH + CuratorUtil.DEVICE_PATH + localHost + remoteHost;
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        CuratorUtil.deleteNode(zkClient, connectPath);
    }

    private void registerDeviceMessageDeal (AbstractMonitorMessage message) {
        DeviceRegisterMessage registerDeviceMessage = (DeviceRegisterMessage) message;
        String host = registerDeviceMessage.getHost();
        String devicePath =
                CuratorUtil.ZK_REGISTER_ROOT_PATH + CuratorUtil.DEVICE_PATH + "/" + host;
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        CuratorUtil.createPersistentNode(zkClient, devicePath);
    }

    private void reportRpcMessageDeal (AbstractMonitorMessage message) {
        ReportRpcMessage reportRpcMessage = (ReportRpcMessage) message;
        String serviceName = reportRpcMessage.getServiceName();
        String address = reportRpcMessage.getAddress();
        String reportPath =
                CuratorUtil.ZK_REGISTER_ROOT_PATH + CuratorUtil.SERVICE_PATH + "/" + serviceName + address;
        CuratorFramework zkClient = CuratorUtil.getZkClient(monitorCenterAddress);
        CuratorUtil.setRpcTimes(zkClient, reportPath);
    }
}
