package com.kalew515.remoting.handler;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.Message;
import com.kalew515.exchange.messages.HeartBeatResponse;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.transport.netty.client.UnprocessedRequests;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyRpcClientHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UnprocessedRequests unprocessedRequests;

    public NettyRpcClientHandler () {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            logger.info("client receive msg: [{}]", msg);
            if (msg instanceof Message) {
                Message message = (Message) msg;
                int messageType = message.getMessageType();
                if (messageType == Message.HEARTBEAT_TYPE_RESPONSE) {
                    HeartBeatResponse heartBeatResponse = (HeartBeatResponse) message;
                    logger.info("heart [{}]", heartBeatResponse.getPong());
                } else {
                    RpcResponse<?> rpcResponse = (RpcResponse<?>) message;
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

}
