package com.kalew515.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static com.kalew515.config.constants.defaultconfig.RpcDefaultConfig.ANYHOST_VALUE;
import static com.kalew515.config.constants.defaultconfig.RpcDefaultConfig.LOCALHOST_VALUE;
import static com.kalew515.utils.CollectionUtil.first;
import static java.util.Collections.emptyList;

public class NetUtils {

    private static final Logger logger = LoggerFactory.getLogger(NetUtils.class);
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private static volatile String HOST_ADDRESS;
    private static volatile InetAddress LOCAL_ADDRESS = null;

    public static String getLocalHost () {
        if (HOST_ADDRESS != null) {
            return HOST_ADDRESS;
        }

        InetAddress address = getLocalAddress();
        if (address != null) {
            return HOST_ADDRESS = address.getHostAddress();
        }
        return LOCALHOST_VALUE;
    }

    public static InetAddress getLocalAddress () {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    private static InetAddress getLocalAddress0 () {
        InetAddress localAddress = null;
        try {
            NetworkInterface networkInterface = findNetworkInterface();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            return addressOp.get();
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getCause().getLocalizedMessage());
        }

        try {
            localAddress = InetAddress.getLocalHost();
            Optional<InetAddress> addressOp = toValidAddress(localAddress);
            if (addressOp.isPresent()) {
                return addressOp.get();
            }
        } catch (Throwable e) {
            logger.warn(e.getCause().getLocalizedMessage());
        }


        return localAddress;
    }

    public static NetworkInterface findNetworkInterface () {

        List<NetworkInterface> validNetworkInterfaces = emptyList();
        try {
            validNetworkInterfaces = getValidNetworkInterfaces();
        } catch (Throwable e) {
            logger.warn(e.getCause().getLocalizedMessage());
        }

        NetworkInterface result = null;

        // If not found, try to get the first one
        for (NetworkInterface networkInterface : validNetworkInterfaces) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            return networkInterface;
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
        }

        result = first(validNetworkInterfaces);

        return result;
    }

    private static List<NetworkInterface> getValidNetworkInterfaces () throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            validNetworkInterfaces.add(networkInterface);
        }
        return validNetworkInterfaces;
    }

    private static Optional<InetAddress> toValidAddress (InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIPV6Address()) {
                return Optional.ofNullable(normalizeV6Address(v6Address));
            }
        }
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    static boolean isPreferIPV6Address () {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    static InetAddress normalizeV6Address (Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException e) {
                // ignore
                logger.debug("Unknown IPV6 address: ", e);
            }
        }
        return address;
    }

    static boolean isValidV4Address (InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !ANYHOST_VALUE.equals(name)
                && !LOCALHOST_VALUE.equals(name));
    }

    public static boolean isInvalidPort (int port) {
        return port < MIN_PORT || port > MAX_PORT;
    }

    public static Integer parsePort (String configPort) {
        Integer port = null;
        if (!StringUtils.isBlank(configPort)) {
            try {
                int intPort = Integer.parseInt(configPort);
                if (isInvalidPort(intPort)) {
                    throw new IllegalArgumentException(
                            "Specified invalid port from env value:" + configPort);
                }
                port = intPort;
            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Specified invalid port from env value:" + configPort);
            }
        }
        return port;
    }

}
