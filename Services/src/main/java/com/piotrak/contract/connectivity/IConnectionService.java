package com.piotrak.contract.connectivity;

import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.types.ConnectivityType;

import java.util.List;

public interface IConnectionService {
    
    IConnection getConnection();
    
    List<Module> getModulesList();
    
    void actOnCommand(ICommand command);
    
    void startService();
    
    void config(List<Module> modules, IConnection connection);
    
    ConnectivityType getConnectivityType();
    
}
