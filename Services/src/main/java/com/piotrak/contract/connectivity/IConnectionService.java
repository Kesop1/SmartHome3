package com.piotrak.contract.connectivity;

import com.piotrak.contract.modularity.actors.IActor;
import com.piotrak.contract.modularity.modules.Module;

import java.util.List;

public interface IConnectionService {
    
    IConnection getConnection();
    
    List<Module> getModulesList();
    
    void actOnCommand(ICommand command);
    
    void startService();
    
    void config(List<Module> modules, IConnection connection);
    
    IActor getActor();
    
}
