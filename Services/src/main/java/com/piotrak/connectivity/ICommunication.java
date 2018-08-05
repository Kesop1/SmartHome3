package com.piotrak.connectivity;

import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Map;

public interface ICommunication {
    
    Map<String, String> getCommunication();
    
    void setCommunication(HierarchicalConfiguration communication);
    
}
