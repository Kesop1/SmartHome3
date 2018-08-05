package com.piotrak.connectivity;

import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Map;

public interface ICommunication {
    
    Map<String, String> getCommunicationMap();
    
    void setCommunicationMap(HierarchicalConfiguration communication);
    
}
