package com.kalew515.remoting.transport.netty.client;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Channel> channelMap;

    public ChannelProvider () {
        channelMap = new ConcurrentHashMap<>();
    }

    public Channel get (InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            if (channel != null && channel.isActive()) return channel;
            else channelMap.remove(key);
        }
        return null;
    }

    public void set (InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove (InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        logger.info("Channel map size: [{}]", channelMap.size());
    }
}
