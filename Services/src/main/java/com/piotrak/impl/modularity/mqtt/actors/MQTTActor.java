package com.piotrak.impl.modularity.mqtt.actors;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.actors.IActor;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MQTTActor implements IActor {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTActor.class);
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private Map<String, List<Module>> topicsMap;
    
    @Override
    public List<ICommand> actOnCommand(ICommand command) {
        List<ICommand> newCommands = new ArrayList<>(0);
        if (!command.getConnectivityType().equals(ConnectivityType.MQTT)) {
            LOGGER.error("Incorrect command provided for the MQTTActor");
        } else {
            MQTTCommand mqttCommand = (MQTTCommand) command;
            String topic = mqttCommand.getTopic();
            for (Map.Entry<String, List<Module>> pair : topicsMap.entrySet()) {
                if (pair.getKey().equals(topic)) {
                    List<Module> modules = pair.getValue();
                    modules.forEach(module -> newCommands.addAll(module.getRules().useRules(command, module)));
                    break;
                }
            }
        }
        return newCommands;
    }
    
    public void setTopicsMap(Map<String, List<Module>> topicsMap) {
        this.topicsMap = topicsMap;
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
