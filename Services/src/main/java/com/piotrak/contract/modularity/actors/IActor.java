package com.piotrak.contract.modularity.actors;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;

public interface IActor {
    
    void actOnCommand(ICommand command, IConnection connection);
    
}
