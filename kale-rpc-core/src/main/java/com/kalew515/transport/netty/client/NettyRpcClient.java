package com.kalew515.transport.netty.client;

import com.kalew515.common.factory.SingletonFactory;
import com.kalew515.config.CustomShutdownHook;
import com.kalew515.exchange.Message;
import com.kalew515.exchange.messages.RpcRequest;
import com.kalew515.exchange.messages.RpcResponse;
import com.kalew515.remoting.handler.NettyRpcClientHandler;
import com.kalew515.remoting.handler.ProtocolFrameDecoder;
import com.kalew515.remoting.handler.RpcClientIdleHandler;
import com.kalew515.remoting.handler.RpcMessageCodec;
import com.kalew515.transport.AbstractRpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.kalew515.config.constants.defaultconfig.RpcClientDefaultConfig.*;

public class NettyRpcClient extends AbstractRpcClient {

    private static final RpcMessageCodec RPC_MESSAGE_CODEC = new RpcMessageCodec();
    private static final NettyRpcClientHandler NETTY_RPC_CLIENT_HANDLER =
            new NettyRpcClientHandler();
    private static final RpcClientIdleHandler RPC_CLIENT_IDLE_HANDLER = new RpcClientIdleHandler();
    private static final LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UnprocessedRequests unprocessedRequests;
    private final ChannelProvider channelProvider;
    private final Bootstrap bootstrap;

    public NettyRpcClient () {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        CustomShutdownHook.registerShutdown(eventLoopGroup, eventLoopGroup::shutdownGracefully);
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                         CONNECT_TIMEOUT_MILLIS).handler(
                         new ChannelInitializer<SocketChannel>() {
                             @Override
                             protected void initChannel (SocketChannel ch) throws Exception {
                                 ChannelPipeline pipeline = ch.pipeline();
                                 pipeline.addLast(LOGGING_HANDLER);
                                 pipeline.addLast(new ProtocolFrameDecoder());
                                 pipeline.addLast(
                                         new IdleStateHandler(IDLE_READ_TIME,
                                                              IDLE_WRITE_TIME,
                                                              ALL_IDLE_TIME,
                                                              TimeUnit.MILLISECONDS));
                                 pipeline.addLast(RPC_CLIENT_IDLE_HANDLER);
                                 pipeline.addLast(RPC_MESSAGE_CODEC);
                                 pipeline.addLast(NETTY_RPC_CLIENT_HANDLER);
                             }
                         });
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
        this.channelProvider = SingletonFactory.getInstance(ChannelProvider.class);
    }

    @Override
    public CompletableFuture<RpcResponse<?>> sendRpcRequest (RpcRequest rpcRequest,
                                                             InetSocketAddress inetSocketAddress) {
        CompletableFuture<RpcResponse<?>> resultFuture = new CompletableFuture<>();
        Channel channel = getChannel(inetSocketAddress);
        if (channel.isActive()) {
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            rpcRequest.setSerializer(this.serializationType.getCode());
            rpcRequest.setMessageType(Message.RPC_MESSAGE_TYPE_REQUEST);
            rpcRequest.setCompress(this.compressType.getCode());
            ChannelFuture channelFuture = channel.writeAndFlush(rpcRequest);
            channelFuture.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    logger.info("client send message: [{}]", rpcRequest);
                } else {
                    future.channel().close();
                    resultFuture.completeExceptionally(future.cause());
                    logger.error("send failed: ", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return resultFuture;
    }

    public Channel getChannel (InetSocketAddress inetSocketAddress) {
        Channel channel = channelProvider.get(inetSocketAddress);
        if (channel == null) {
            channel = doConnect(inetSocketAddress);
            channelProvider.set(inetSocketAddress, channel);
        }
        return channel;
    }

    private Channel doConnect (InetSocketAddress inetSocketAddress) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                logger.info("The client has connected [{}] successful!",
                            inetSocketAddress.toString());
                completableFuture.complete(future.channel());
            } else throw new IllegalStateException();
        });
        try {
            return completableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
