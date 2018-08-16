package com.piotrak.impl.modularity.rules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.contract.modularity.rules.IRules;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.types.ConnectivityType;

import java.util.ArrayList;
import java.util.List;

public class RulesMonitor implements IRules {
    
    @Override
    public List<ICommand> useRules(ICommand command, Module module) {
        List<ICommand> newCommands = new ArrayList<>(0);
        if (command.getConnectivityType().equals(ConnectivityType.MQTT)) {
            MQTTCommand commandRule1 = rule1((MQTTCommand) command, module);
            if (commandRule1 != null) {
                newCommands.add(commandRule1);
            }
        }
        return newCommands;
    }
    
    private MQTTCommand rule1(MQTTCommand command, Module module) {
        if (Constants.ON.equals(command.getMessage())) {
            return new MQTTCommand(module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH), "DZIALA");
        }
        return null;
    }
}
