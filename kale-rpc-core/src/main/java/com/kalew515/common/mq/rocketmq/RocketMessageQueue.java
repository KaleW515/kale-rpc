package com.kalew515.common.mq.rocketmq;

import com.kalew515.common.enums.SerializerEnum;
import com.kalew515.common.extension.ExtensionLoader;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.common.mq.AbstractMonitorMessage;
import com.kalew515.common.mq.MessageQueue;
import com.kalew515.config.ConfigCenter;
import com.kalew515.config.ConfigCenterImpl;
import com.kalew515.config.CustomShutdownHook;
import com.kalew515.serialize.Serializer;
import org.apache.rocketmq.client.consumer.DefaultLitePullConsumer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.kalew515.config.constants.RpcConfigConstants.MQ_ADDRESS;

public class RocketMessageQueue implements MessageQueue {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Serializer serializer;

    private final String producerGroup = "kale-rpc-producer";

    private final String consumerGroup = "kale-rpc-consumer";

    private final String topic = "kale-rpc-monitor-msg";

    private final String nameServerAddress;

    private final ConfigCenter configCenter;

    private final DefaultMQProducer producer;

    private final DefaultLitePullConsumer consumer;

    private List<MessageExt> messages = Collections.emptyList();

    private AtomicInteger capacity = new AtomicInteger(0);

    public RocketMessageQueue () {
        this.producer = new DefaultMQProducer(producerGroup);
        this.consumer = new DefaultLitePullConsumer(consumerGroup);

        this.configCenter = SingletonFactory.getInstance(ConfigCenterImpl.class);
        this.nameServerAddress = this.configCenter.getConfig(MQ_ADDRESS);
        this.producerInit();
        this.consumerInit();
        this.serializer = ExtensionLoader.getExtensionLoader(Serializer.class)
                                         .getExtension(SerializerEnum.JSON.getName());
        try {
            this.producer.start();
            this.consumer.start();
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        } finally {
            CustomShutdownHook.registerShutdown(producer, producer::shutdown);
            CustomShutdownHook.registerShutdown(consumer, consumer::shutdown);
        }
    }

    private void producerInit () {
        this.producer.setNamesrvAddr(this.nameServerAddress);
    }

    private void consumerInit () {
        this.consumer.setNamesrvAddr(this.nameServerAddress);
        this.consumer.setAutoCommit(true);
        this.consumer.setMessageModel(MessageModel.CLUSTERING);
        try {
            this.consumer.subscribe(topic, "*");
        } catch (MQClientException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean put (AbstractMonitorMessage message) {
        byte[] serialize = serializer.serialize(message);
        Message msg = new Message(topic, null, serialize);
        try {
            SendResult send = producer.send(msg);
        } catch (MQClientException | RemotingException | MQBrokerException |
                 InterruptedException e) {
            logger.warn(e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    public AbstractMonitorMessage take () {
        if (capacity.get() == messages.size()) {
            capacity.compareAndSet(messages.size(), 0);
            messages = Collections.emptyList();
            while (messages.size() == 0) {
                messages = consumer.poll();
            }
        }
        int idx = capacity.getAndIncrement();
        byte[] body = messages.get(idx).getBody();
        return serializer.deserialize(body, AbstractMonitorMessage.class);
    }
}
