package com.piotrak.impl.modularity.mqtt.actors;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.modularity.actors.IActor;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class MQTTActor implements IActor {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTActor.class);
    
    private Map<String, List<Module>> topicsMap;
    
    public MQTTActor(Map<String, List<Module>> topicsMap) {
        this.topicsMap = topicsMap;
    }
    
    @Override
    public void actOnCommand(ICommand command, IConnection connection) {
        if (!(command instanceof MQTTCommand) || !(connection instanceof MQTTConnection)) {
            LOGGER.error("Incorrect command or connection provided for the MQTTActor");
            return;
        }
        MQTTCommand mqttCommand = (MQTTCommand) command;
        String topic = mqttCommand.getTopic();
        for (Map.Entry<String, List<Module>> pair : topicsMap.entrySet()) {
            if (pair.getKey().equals(topic)) {
                List<Module> modules = pair.getValue();
                modules.forEach(module -> module.getRules().useRules(command, module, connection));
                break;
            }
        }
    }
}
