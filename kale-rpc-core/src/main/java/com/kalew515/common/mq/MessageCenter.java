package com.kalew515.common.mq;

/**
 * message center, for external invocation
 */
public interface MessageCenter {

    /**
     * put message to mq
     *
     * @param message
     * @return
     */
    boolean putMessage (AbstractMonitorMessage message);

    /**
     * take message from mq, this method maybe block current thread
     *
     * @return
     */
    AbstractMonitorMessage takeMessage ();
}
