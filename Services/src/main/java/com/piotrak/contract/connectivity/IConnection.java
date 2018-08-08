package com.piotrak.contract.connectivity;

import com.piotrak.contract.modularity.actors.IActor;
import org.apache.commons.configuration.HierarchicalConfiguration;

public interface IConnection {
    
    void config(HierarchicalConfiguration config);
    
    void connect(IActor actor);
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(ICommand command);
    
}
