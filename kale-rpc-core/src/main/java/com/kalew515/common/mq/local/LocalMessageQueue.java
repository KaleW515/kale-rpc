package com.kalew515.common.mq.local;

import com.kalew515.common.mq.AbstractMonitorMessage;
import com.kalew515.common.mq.MessageQueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LocalMessageQueue implements MessageQueue {

    private final BlockingQueue<AbstractMonitorMessage> messages;

    public LocalMessageQueue () {
        this.messages = new LinkedBlockingQueue<>();
    }

    @Override
    public boolean put (AbstractMonitorMessage message) {
        try {
            this.messages.put(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public AbstractMonitorMessage take () {
        try {
            return this.messages.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
