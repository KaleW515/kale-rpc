package com.kalew515.config.constants.defaultconfig;

public class RpcServerDefaultConfig {

    public static final Integer IDLE_READ_TIME = 30000;

    public static final Integer IDLE_WRITE_TIME = 0;

    public static final Integer ALL_IDLE_TIME = 0;

    public static final Integer SO_BACKLOG = 128;

    public static final Boolean TCP_KEEP_ALIVE = true;

    public static final Boolean TCP_NODELAY = true;

    public static final Integer DEFAULT_TRANSPORTER_SERVER_PORT = 12800;

    public static final String DEFAULT_TRANSPORTER = "netty";

    public static final String DEFAULT_MQ = "local";

    // for rocket mq
    public static final String DEFAULT_MQ_ADDRESS = "127.0.0.1:9876";
}
