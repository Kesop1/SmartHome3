package com.piotrak.contract.modularity.actors;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.impl.types.ConnectivityType;

import java.util.List;

public interface IActor {
    
    List<ICommand> actOnCommand(ICommand command);
    
    ConnectivityType getConnectivityType();
    
}
