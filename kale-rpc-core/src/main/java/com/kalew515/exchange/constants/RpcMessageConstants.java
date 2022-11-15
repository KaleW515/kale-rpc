package com.kalew515.exchange.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class RpcMessageConstants {

    public static final byte[] MAGIC_NUMBER = {0x02, 0x02, 0x03, 0x02};

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final byte VERSION = 1;

    public static final byte TOTAL_LENGTH = 16;

    public static final int HEAD_LENGTH = 16;

    public static final int MAX_FRAME_LENGTH = 8 * 1024 * 1024;
}
