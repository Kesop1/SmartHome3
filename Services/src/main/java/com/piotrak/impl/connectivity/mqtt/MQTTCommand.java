package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.contract.connectivity.ICommand;

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
