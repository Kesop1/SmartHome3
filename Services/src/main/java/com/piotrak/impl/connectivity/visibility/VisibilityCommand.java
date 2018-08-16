package com.piotrak.impl.connectivity.visibility;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.impl.types.ConnectivityType;

public class VisibilityCommand implements ICommand {
    
    private ConnectivityType connectivityType = ConnectivityType.VISIBILITY;
    
    private String commandText;
    
    private int commandValue;
    
    public VisibilityCommand(String commandText, int commandValue) {
        this.commandText = commandText;
        this.commandValue = commandValue;
    }
    
    public String getCommandText() {
        return commandText;
    }
    
    public int getCommandValue() {
        return commandValue;
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
