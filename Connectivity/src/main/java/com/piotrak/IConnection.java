package com.piotrak;

import org.apache.commons.configuration.HierarchicalConfiguration;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "connection")
public interface IConnection {
    
    String SEPARATOR = "://";
    
    void config(HierarchicalConfiguration config);
    
    void connect();
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(ICommand command);
    
    ICommand getCommand();
}
