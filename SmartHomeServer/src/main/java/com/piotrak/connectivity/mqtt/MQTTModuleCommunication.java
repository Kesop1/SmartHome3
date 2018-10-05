package com.piotrak.connectivity.mqtt;

import com.piotrak.ServerConstants;
import com.piotrak.connectivity.IModuleCommunication;
import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MQTTModuleCommunication implements IModuleCommunication {
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private Map<String, String> communicationMap = new HashMap<>(0);
    
    @Override
    public Map<String, String> getCommunicationMap() {
        return new HashMap<>(communicationMap);
    }
    
    @Override
    public void setCommunicationMap(HierarchicalConfiguration config) {
        communicationMap.put(ServerConstants.MQTT_TOPIC_SUBSCRIBE, config.getString(ServerConstants.MQTT_TOPIC_SUBSCRIBE));
        communicationMap.put(ServerConstants.MQTT_TOPIC_PUBLISH, config.getString(ServerConstants.MQTT_TOPIC_PUBLISH));
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
