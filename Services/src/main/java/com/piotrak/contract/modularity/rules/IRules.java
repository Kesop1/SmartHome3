package com.piotrak.contract.modularity.rules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;

import java.util.ArrayList;
import java.util.List;

public interface IRules {
    
    List<ICommand> useRules(ICommand command, Module module);
    
    /**
     * default implementation, send out the message received to the module's connection
     *
     * @param command
     * @param module
     */
    default List<ICommand> onButtonClick(VisibilityCommand command, Module module) {
        List<ICommand> newCommands = new ArrayList<>(0);
        if (Constants.MQTT.equals(module.getCommunication().getConnectivityType())) {
            newCommands.add(new MQTTCommand(module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE), command.getCommandText()));
        }
        return newCommands;
    }
}
