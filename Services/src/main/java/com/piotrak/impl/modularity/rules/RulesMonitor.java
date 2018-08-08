package com.piotrak.impl.modularity.rules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.contract.modularity.rules.IRules;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;

public class RulesMonitor implements IRules {
    
    @Override
    public void useRules(ICommand command, Module module, IConnection connection) {
        if (command instanceof MQTTCommand && connection instanceof MQTTConnection) {
            rule1((MQTTCommand) command, module, (MQTTConnection) connection);
        }
    }
    
    private void rule1(MQTTCommand command, Module module, MQTTConnection connection) {
        if (Constants.ON.equals(command.getMessage())) {
            MQTTCommand mqttCommand = new MQTTCommand(module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH), "DZIALA");
            connection.sendCommand(mqttCommand);
        }
    }
}
