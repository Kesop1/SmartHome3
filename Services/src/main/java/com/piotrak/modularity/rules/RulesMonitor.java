package com.piotrak.modularity.rules;

import com.piotrak.Constants;
import com.piotrak.connectivity.ICommand;
import com.piotrak.connectivity.IConnection;
import com.piotrak.connectivity.mqtt.MQTTCommand;
import com.piotrak.modularity.modules.Module;

public class RulesMonitor implements IRules {
    
    @Override
    public void useRules(ICommand command, Module module, IConnection connection) {
        if (command instanceof MQTTCommand) {
            MQTTCommand mqttCommand = (MQTTCommand) command;
            rule1(mqttCommand, module, connection);
        }
        
    }
    
    private void rule1(MQTTCommand command, Module module, IConnection connection) { //TODO: potrzeba mi tu Module?
        if (Constants.ON.equals(command.getMessage())) {
            MQTTCommand mqttCommand = new MQTTCommand(module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH), "DZIALA");
            connection.sendCommand(mqttCommand);
        }
    }
}
