package com.kalew515.common.mq;

import com.kalew515.common.extension.SPI;

/**
 * message queue, for asynchronous message consumption
 */
@SPI
public interface MessageQueue {

    /**
     * put message to mq, you should call this by MessageCenter.putMessage(AbstractMonitorMessage
     * message)
     *
     * @param message
     * @return
     */
    boolean put (AbstractMonitorMessage message);

    /**
     * take message from mq, this method maybe block current thread if there are no messages, you
     * should call this by MessageCenter.takeMessage()
     *
     * @return
     */
    AbstractMonitorMessage take ();
}
