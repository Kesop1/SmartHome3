package com.piotrak.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.connectivity.ICommunication;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MQTTCommunication implements ICommunication {
    
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
}
