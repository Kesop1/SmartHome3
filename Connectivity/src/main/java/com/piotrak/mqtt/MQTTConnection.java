package com.piotrak.mqtt;

import com.piotrak.ICommand;
import com.piotrak.IConnection;
import org.apache.commons.configuration.HierarchicalConfiguration;

public class MQTTConnection implements IConnection {
    
    private String host;
    private String port;
    private String protocol;
    
    @Override
    public void config(HierarchicalConfiguration config) {
        host = config.getString("host") == null ? "0.0.0.0" : config.getString("host");
        port = config.getString("port") == null ? "0" : config.getString("port");
        protocol = config.getString("protocol") == null ? "tcp" : config.getString("protocol");
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
    
    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getProtocol() {
        return protocol;
    }
}
