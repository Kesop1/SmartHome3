package com.piotrak.contract.connectivity;

import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.Map;

public interface ICommunication {
    
    Map<String, String> getCommunicationMap();
    
    void setCommunicationMap(HierarchicalConfiguration communication);
    
    ConnectivityType getConnectivityType();
    
}
