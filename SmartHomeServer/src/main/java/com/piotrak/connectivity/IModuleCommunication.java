package com.piotrak.connectivity;

import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Map;

public interface IModuleCommunication {
    
    Map<String, String> getCommunicationMap();
    
    void setCommunicationMap(HierarchicalConfiguration communication);
    
    ConnectivityType getConnectivityType();
    
}
