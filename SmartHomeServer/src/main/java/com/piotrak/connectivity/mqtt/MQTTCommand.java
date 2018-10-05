package com.piotrak.connectivity.mqtt;

import com.piotrak.connectivity.Command;
import com.piotrak.types.ConnectivityType;

public class MQTTCommand extends Command {
    
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
    
    @Override
    public String toString() {
        return this.getClass().getName() + ": topic=" + topic + ", message=" + message;
    }
}
