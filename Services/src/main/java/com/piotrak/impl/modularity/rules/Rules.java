package com.piotrak.impl.modularity.rules;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;
import com.piotrak.impl.types.ConnectivityType;
import com.piotrak.impl.types.ModuleType;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.piotrak.Constants.MQTT_TOPIC_PUBLISH;

public class Rules {
    
    public static final Logger LOGGER = Logger.getLogger(Rules.class);
    
    private Map<ConnectivityType, IConnectionService> iConnectionServiceMap;
    
    private List<Module> moduleList;
    
    public Rules(Map<ConnectivityType, IConnectionService> iConnectionServiceMap, List<Module> moduleList) {
        this.iConnectionServiceMap = iConnectionServiceMap;
        this.moduleList = moduleList;
    }
    
    public void act(ICommand command) {
        ruleMouseClickSwitchElementSendToMQTT(command);
    }
    
    void ruleMouseClickSwitchElementSendToMQTT(ICommand command) {
        try {
            VisibilityCommand visibilityCommand = (VisibilityCommand) command;
            if (visibilityCommand.getModule().getCommunication().getConnectivityType() == ConnectivityType.MQTT &&
                    visibilityCommand.getModule().getModuleType() == ModuleType.SWITCH) {
                MQTTConnectionService service = (MQTTConnectionService) iConnectionServiceMap.get(ConnectivityType.MQTT);
                MQTTCommand mqttCommand = new MQTTCommand(visibilityCommand.getModule().getCommunication().getCommunicationMap().get(MQTT_TOPIC_PUBLISH),
                        visibilityCommand.getCommandText());
                service.getConnection().sendCommand(mqttCommand);
                if ("Amplituner".equals(visibilityCommand.getModule().getName())) {
                    Module subwoofer = null;
                    for (Module module : moduleList) {
                        if ("Subwoofer".equals(module.getName())) {
                            subwoofer = module;
                            break;
                        }
                    }
                    if (subwoofer == null) {
                        LOGGER.info("Subwoofer module not found for the Amplituner module");
                        return;
                    }
                    MQTTCommand mqttCommandSub = new MQTTCommand(subwoofer.getCommunication().getCommunicationMap().get(MQTT_TOPIC_PUBLISH),
                            visibilityCommand.getCommandText());
                    service.getConnection().sendCommand(mqttCommandSub);
                }
            }
        } catch (ClassCastException cce) {
            LOGGER.debug("Not a VisibilityCommand, will not use ruleMouseClickSwitchElementSendToMQTT");
        }
    }
}
