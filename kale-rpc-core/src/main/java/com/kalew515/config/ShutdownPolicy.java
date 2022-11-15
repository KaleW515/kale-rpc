package com.kalew515.config;

/**
 * shutdown policy interface, for resource shutdown
 */
@FunctionalInterface
public interface ShutdownPolicy {

    void shutdown ();
}
