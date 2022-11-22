package com.kalew515.remoting.transport.netty.handler;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.monitor.MonitorCenter;
import com.kalew515.monitor.MonitorCenterImpl;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class RpcServerMonitorHandler extends ChannelDuplexHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MonitorCenter monitorCenter;

    public RpcServerMonitorHandler () {
        this.monitorCenter = SingletonFactory.getInstance(MonitorCenterImpl.class);
    }

    @Override
    public void channelActive (ChannelHandlerContext ctx) throws Exception {
        this.monitorCenter.reportConn(
                ctx.channel().localAddress().toString().split(":")[0],
                ctx.channel().remoteAddress().toString());
        logger.debug("report connect to register center");
    }

    @Override
    public void channelInactive (ChannelHandlerContext ctx) throws Exception {
        this.monitorCenter.reportDisconn(
                ctx.channel().localAddress().toString().split(":")[0],
                ctx.channel().remoteAddress().toString());
        logger.debug("report disconnect to register center");
    }

    @Override
    public void channelRead (ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcRequest) {
            this.monitorCenter.reportRpc(
                    ((RpcRequest) msg).getRpcServiceName(),
                    ctx.channel().localAddress().toString());
        }
        super.channelRead(ctx, msg);
    }
}
