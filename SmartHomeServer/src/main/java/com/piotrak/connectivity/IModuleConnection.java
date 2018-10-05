package com.piotrak.connectivity;

import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

public interface IModuleConnection {
    
    void config(HierarchicalConfiguration config);
    
    void connect();
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(Command command);
    
    ConnectivityType getConnectivityType();
    
}
