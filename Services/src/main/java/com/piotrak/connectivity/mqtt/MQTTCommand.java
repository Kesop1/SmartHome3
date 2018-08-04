package com.piotrak.connectivity.mqtt;

import com.piotrak.connectivity.ICommand;

public class MQTTCommand implements ICommand {
    
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
}
