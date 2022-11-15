package com.kalew515.common.spring.event;

import org.springframework.context.ApplicationEvent;

public class RpcServerStartedEvent extends ApplicationEvent {

    public RpcServerStartedEvent (Object source) {
        super(source);
    }
}
