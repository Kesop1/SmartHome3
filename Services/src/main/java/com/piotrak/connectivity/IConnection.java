package com.piotrak.connectivity;

import com.piotrak.modularity.Module;
import org.apache.commons.configuration.HierarchicalConfiguration;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;

@XmlRootElement(name = "connection")
public interface IConnection {
    
    void config(HierarchicalConfiguration config, Map<String, Module> topicsMap);
    
    void connect();
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(ICommand command);
    
    void listenForCommand();
}
