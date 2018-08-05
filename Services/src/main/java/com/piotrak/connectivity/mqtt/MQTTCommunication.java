package com.piotrak.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.connectivity.ICommunication;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.HashMap;
import java.util.Map;

public class MQTTCommunication implements ICommunication {
    
    private Map<String, String> communication = new HashMap<>(0);
    
    @Override
    public Map<String, String> getCommunication() {
        return communication;
    }
    
    @Override
    public void setCommunication(HierarchicalConfiguration config) {
        communication.put(Constants.MQTT_TOPIC_SUBSCRIBE, config.getString(Constants.MQTT_TOPIC_SUBSCRIBE));
        communication.put(Constants.MQTT_TOPIC_PUBLISH, config.getString(Constants.MQTT_TOPIC_PUBLISH));
    }
}
