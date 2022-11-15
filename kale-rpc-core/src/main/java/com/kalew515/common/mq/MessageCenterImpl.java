package com.kalew515.common.mq;

import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;

import static com.kalew515.config.constants.RpcConfigConstants.MQ;

public class MessageCenterImpl implements MessageCenter {

    private final ConfigCenter configCenter;

    private final MessageQueue messageQueue;

    public MessageCenterImpl () {
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        String mqName = this.configCenter.getConfig(MQ);
        this.messageQueue = ExtensionLoader.getExtensionLoader(MessageQueue.class)
                                           .getExtension(mqName);
    }

    public MessageCenterImpl (MessageQueue messageQueue) {
        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.messageQueue = messageQueue;
    }

    @Override
    public boolean putMessage (AbstractMonitorMessage message) {
        return this.messageQueue.put(message);
    }

    @Override
    public AbstractMonitorMessage takeMessage () {
        return this.messageQueue.take();
    }
}
