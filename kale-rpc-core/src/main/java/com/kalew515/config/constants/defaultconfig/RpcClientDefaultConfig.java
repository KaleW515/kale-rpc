package com.kalew515.config.constants.defaultconfig;

import java.util.Random;

public class RpcClientDefaultConfig {

    public static final Integer CONNECT_TIMEOUT_MILLIS = 30000;

    public static final Integer IDLE_READ_TIME = 0;

    // To avoid the simultaneous heartbeat
    public static final Integer IDLE_WRITE_TIME = 15000 + (new Random().nextInt(2000));

    public static final Integer ALL_IDLE_TIME = 0;

    public static final String DEFAULT_SERIALIZATION = "kryo";

    public static final String DEFAULT_COMPRESS = "gzip";

    public static final String DEFAULT_TRANSPORTER = "netty";

    public static final String DEFAULT_LOAD_BALANCER = "random";

    public static final Integer DEFAULT_TIMEOUT = 600;

    public static final String DEFAULT_FAIL_STRATEGY = "fail-safe";
}
