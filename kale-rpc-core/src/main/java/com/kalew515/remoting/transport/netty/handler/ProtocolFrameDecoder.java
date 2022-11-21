package com.kalew515.remoting.transport.netty.handler;

import com.kalew515.exchange.constants.RpcMessageConstants;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class ProtocolFrameDecoder extends LengthFieldBasedFrameDecoder {

    public ProtocolFrameDecoder () {
        this(RpcMessageConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    public ProtocolFrameDecoder (int maxFrameLength, int lengthFieldOffset, int lengthFieldLength
            , int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment,
              initialBytesToStrip);
    }
}
