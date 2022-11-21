package com.kalew515.transport.netty.server;

import com.kalew515.config.CustomShutdownHook;
import com.kalew515.remoting.handler.*;
import com.kalew515.transport.AbstractRpcServer;
import com.kalew515.utils.ThreadPoolFactoryUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;

import java.util.concurrent.TimeUnit;

import static com.kalew515.config.constants.defaultconfig.RpcServerDefaultConfig.*;

public class NettyRpcServer extends AbstractRpcServer {

    private static final RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();
    private static final NettyRpcServerHandler NETTY_RPC_SERVER_HANDLER =
            new NettyRpcServerHandler();
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler();
    private static final RpcServerIdleHandler RPC_SERVER_IDLE_HANDLER = new RpcServerIdleHandler();
    private static final RpcServerMonitorHandler RPC_SERVER_MONITOR_HANDLER =
            new RpcServerMonitorHandler();
    private NioEventLoopGroup bossGroup;

    private NioEventLoopGroup workerGroup;

    private DefaultEventExecutorGroup serviceHandlerGroup;

    public NettyRpcServer () {
    }


    @Override
    protected void doRpcServerStart (String host, int port) {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        this.serviceHandlerGroup = new DefaultEventExecutorGroup(Runtime.getRuntime()
                                                                        .availableProcessors() * 2,
                                                                 ThreadPoolFactoryUtil.createThreadFactory(
                                                                         "service-handler-group",
                                                                         false));
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(this.bossGroup, this.workerGroup)
                           .channel(NioServerSocketChannel.class)
                           .childOption(ChannelOption.TCP_NODELAY,
                                        TCP_NODELAY)
                           .childOption(ChannelOption.SO_KEEPALIVE,
                                        TCP_KEEP_ALIVE)
                           .option(ChannelOption.SO_BACKLOG, SO_BACKLOG)
                           .handler(LOGGING_HANDLER)
                           .childHandler(new ChannelInitializer<SocketChannel>() {
                               @Override
                               protected void initChannel (SocketChannel ch) throws Exception {
                                   ChannelPipeline pipeline = ch.pipeline();
                                   pipeline.addLast(new ProtocolFrameDecoder());
                                   pipeline.addLast(LOGGING_HANDLER);
                                   pipeline.addLast(
                                           new IdleStateHandler(IDLE_READ_TIME, IDLE_WRITE_TIME,
                                                                ALL_IDLE_TIME,
                                                                TimeUnit.MILLISECONDS));
                                   pipeline.addLast(RPC_SERVER_IDLE_HANDLER);
                                   pipeline.addLast(RPC_MESSAGE_CODEC);
                                   pipeline.addLast(RPC_SERVER_MONITOR_HANDLER);
                                   pipeline.addLast(serviceHandlerGroup, NETTY_RPC_SERVER_HANDLER);
                               }
                           });
            serverBootstrap.bind(host, port).sync();
            logger.info("wait for connecting....");
            // shutdown executors by shutdown hook
            registerGroupShutdown();
        } catch (InterruptedException e) {
            logger.error("occur exception when start server:", e);
        }
    }

    private void registerGroupShutdown () {
        CustomShutdownHook.registerShutdown(bossGroup, bossGroup::shutdownGracefully);
        CustomShutdownHook.registerShutdown(workerGroup, workerGroup::shutdownGracefully);
        CustomShutdownHook.registerShutdown(serviceHandlerGroup,
                                            serviceHandlerGroup::shutdownGracefully);
    }
}
