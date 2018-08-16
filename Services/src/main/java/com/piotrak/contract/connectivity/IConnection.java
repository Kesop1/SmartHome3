package com.piotrak.contract.connectivity;

import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

public interface IConnection {
    
    void config(HierarchicalConfiguration config);
    
    void connect(ActorsService actorsService);
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(ICommand command);
    
    ConnectivityType getConnectivityType();
    
}
