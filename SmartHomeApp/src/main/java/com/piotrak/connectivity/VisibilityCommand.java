package com.piotrak.connectivity;

import com.piotrak.modularity.Module;
import com.piotrak.types.ConnectivityType;

import static com.piotrak.servers.ServerCnsts.VISIBILITY_CMD;

public class VisibilityCommand extends Command {
    
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
    
    @Override
    public String toString() {
        return VISIBILITY_CMD + "module=" + module.getName() + ", commandText=" + commandText +
                ", commandValue=" + commandValue;
    }
}
