package com.kalew515.remoting.transport.netty.handler;

import com.kalew515.common.enums.RpcResponseStatusEnum;
import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.Message;
import com.kalew515.exchange.messages.HeartBeatResponse;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class NettyRpcServerHandler extends ChannelInboundHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RpcRequestHandler rpcRequestHandler;

    public NettyRpcServerHandler () {
        this.rpcRequestHandler = SingletonFactory.getInstance(RpcRequestHandler.class);
    }

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof Message) {
                logger.info("server receive msg: [{}]", msg);
                int messageType = ((Message) msg).getMessageType();
                Message response = null;
                if (messageType == Message.HEARTBEAT_TYPE_REQUEST) {
                    response = new HeartBeatResponse();
                    response.setMessageType(Message.HEARTBEAT_TYPE_RESPONSE);
                } else {
                    RpcRequest rpcRequest = (RpcRequest) msg;
                    Object result = rpcRequestHandler.handle(rpcRequest);
                    logger.info(String.format("server get result: %s", result.toString()));
                    if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                        response = new RpcResponse<>(((RpcRequest) msg).getRequestId(),
                                                     RpcResponseStatusEnum.SUCCESS.getMessage(),
                                                     RpcResponseStatusEnum.SUCCESS.getCode(),
                                                     result);
                        response.setMessageType(Message.RPC_MESSAGE_TYPE_RESPONSE);
                    } else {
                        response = new RpcResponse<>(((RpcRequest) msg).getRequestId(),
                                                     RpcResponseStatusEnum.FAIL.getMessage(),
                                                     RpcResponseStatusEnum.FAIL.getCode(), result);
                        response.setMessageType(Message.RPC_MESSAGE_TYPE_RESPONSE);
                        logger.error("not writable now, message dropped");
                    }
                }
                response.setRequestId(((Message) msg).getRequestId());
                response.setSerializer(((Message) msg).getSerializer());
                response.setCompress(((Message) msg).getCompress());
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
