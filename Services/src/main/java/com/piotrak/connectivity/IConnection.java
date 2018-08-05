package com.piotrak.connectivity;

import com.piotrak.modularity.modules.Module;
import org.apache.commons.configuration.HierarchicalConfiguration;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "connection")
public interface IConnection {
    
    void config(HierarchicalConfiguration config, List<Module> modules);
    
    void connect();
    
    boolean isConnected();
    
    void disconnect();
    
    void sendCommand(ICommand command);
    
    void listenForCommand();
}
