package com.piotrak.connectivity;


import com.piotrak.types.ConnectivityType;

/**
 * Command for each connectivity system, like MQTT
 */
public abstract class Command {
    
    /**
     * Connectivity type for the Command
     *
     * @return ConnectivityType
     */
    public abstract ConnectivityType getConnectivityType();
    
}
