package com.piotrak.mqtt;

import com.piotrak.ICommand;
import com.piotrak.IConnection;
import org.apache.commons.configuration.HierarchicalConfiguration;

public class MQTTConnection implements IConnection {
    
    private String HOST = "0.0.0.0";
    private String PORT = "0";
    private String PROTOCOL = "tcp";
    
    @Override
    public void config(HierarchicalConfiguration config) {
    
    }
    
    @Override
    public void connect() {
    
    }
    
    @Override
    public boolean isConnected() {
        return false;
    }
    
    @Override
    public void disconnect() {
    
    }
    
    @Override
    public void sendCommand(ICommand command) {
    
    }
    
    @Override
    public ICommand getCommand() {
        return null;
    }
}
