package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.impl.types.ConnectivityType;

public class MQTTCommand implements ICommand {
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private String topic;
    
    private String message;
    
    public MQTTCommand(String topic, String message) {
        this.topic = topic;
        this.message = message;
    }
    
    public String getTopic() {
        return topic;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
