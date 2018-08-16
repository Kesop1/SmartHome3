package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommunication;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MQTTCommunication implements ICommunication {
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private Map<String, String> communicationMap = new HashMap<>(0);
    
    @Override
    public Map<String, String> getCommunicationMap() {
        return new HashMap<>(communicationMap);
    }
    
    @Override
    public void setCommunicationMap(HierarchicalConfiguration config) {
        communicationMap.put(Constants.MQTT_TOPIC_SUBSCRIBE, config.getString(Constants.MQTT_TOPIC_SUBSCRIBE));
        communicationMap.put(Constants.MQTT_TOPIC_PUBLISH, config.getString(Constants.MQTT_TOPIC_PUBLISH));
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
