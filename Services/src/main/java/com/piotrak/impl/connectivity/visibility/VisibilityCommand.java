package com.piotrak.impl.connectivity.visibility;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.types.ConnectivityType;

public class VisibilityCommand implements ICommand {
    
    private ConnectivityType connectivityType = ConnectivityType.VISIBILITY;
    
    private String commandText;
    
    private int commandValue;
    
    private Module module;
    
    public VisibilityCommand(String commandText, int commandValue, Module module) {
        this.commandText = commandText;
        this.commandValue = commandValue;
        this.module = module;
    }
    
    public String getCommandText() {
        return commandText;
    }
    
    public int getCommandValue() {
        return commandValue;
    }
    
    public Module getModule() {
        return module;
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
