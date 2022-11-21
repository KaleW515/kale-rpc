package com.kalew515.remoting.handler;

import com.kalew515.common.enums.CompressEnum;
import com.kalew515.common.enums.SerializerEnum;
import com.kalew515.exchange.Message;
import com.kalew515.exchange.messages.HeartBeatRequest;
import io.netty.channel.*;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RpcClientIdleHandler extends ChannelDuplexHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void userEventTriggered (ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.WRITER_IDLE) {
                logger.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = ctx.channel();
                HeartBeatRequest heartBeatRequest = new HeartBeatRequest(0L);
                heartBeatRequest.setMessageType(Message.HEARTBEAT_TYPE_REQUEST);
                heartBeatRequest.setSerializer(SerializerEnum.KRYO.getCode());
                heartBeatRequest.setCompress(CompressEnum.GZIP.getCode());
                channel.writeAndFlush(heartBeatRequest)
                       .addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught (ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client catch exception: ", cause);
        cause.printStackTrace();
        ctx.close();
    }

}
