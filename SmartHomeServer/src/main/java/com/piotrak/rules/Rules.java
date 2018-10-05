package com.piotrak.rules;

import com.piotrak.connectivity.Command;
import com.piotrak.connectivity.IConnectionService;
import com.piotrak.connectivity.VisibilityCommand;
import com.piotrak.connectivity.mqtt.MQTTCommand;
import com.piotrak.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.types.ConnectivityType;
import com.piotrak.types.ModuleType;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

import static com.piotrak.ServerConstants.MQTT_TOPIC_PUBLISH;

public class Rules {
    
    public static final Logger LOGGER = Logger.getLogger(Rules.class);
    
    private Map<ConnectivityType, IConnectionService> iConnectionServiceMap;
    
    private List<ServerModule> moduleList;
    
    public Rules(Map<ConnectivityType, IConnectionService> iConnectionServiceMap, List<ServerModule> moduleList) {
        this.iConnectionServiceMap = iConnectionServiceMap;
        this.moduleList = moduleList;
    }
    
    public void act(Command command) {
        LOGGER.debug("Applying rules on command: " + command.toString());
        if (command instanceof VisibilityCommand) {
            VisibilityCommand visibilityCommand = (VisibilityCommand) command;
            ServerModule module = null;
            for (ServerModule m : moduleList) {
                if (m.getName().equals(visibilityCommand.getModule().getName())) {
                    module = m;
                    break;
                }
            }
            ruleMouseClickSwitchElementSendToMQTT((VisibilityCommand) command, module);
        }
        
    }
    
    private void ruleMouseClickSwitchElementSendToMQTT(VisibilityCommand command, ServerModule module) {
        try {
            if (module != null && module.getCommunication().getConnectivityType() == ConnectivityType.MQTT &&
                    module.getModuleType() == ModuleType.SWITCH) {
                LOGGER.debug(command.toString() + " is applicable to ruleMouseClickSwitchElementSendToMQTT");
                MQTTConnectionService service = (MQTTConnectionService) iConnectionServiceMap.get(ConnectivityType.MQTT);
                MQTTCommand mqttCommand = new MQTTCommand(module.getCommunication().getCommunicationMap().get(MQTT_TOPIC_PUBLISH),
                        command.getCommandText());
                service.getConnection().sendCommand(mqttCommand);
                if ("Amplituner".equals(module.getName())) {
                    ServerModule subwoofer = null;
                    for (ServerModule m : moduleList) {
                        if ("Subwoofer".equals(m.getName())) {
                            subwoofer = m;
                            break;
                        }
                    }
                    if (subwoofer == null) {
                        LOGGER.info("Subwoofer module not found for the module");
                        return;
                    }
                    MQTTCommand mqttCommandSub = new MQTTCommand(subwoofer.getCommunication().getCommunicationMap().get(MQTT_TOPIC_PUBLISH),
                            command.getCommandText());
                    service.getConnection().sendCommand(mqttCommandSub);
                }
            }
        } catch (ClassCastException cce) {
            LOGGER.debug("Not a VisibilityCommand, will not use ruleMouseClickSwitchElementSendToMQTT");
        }
    }
    
    public List<ServerModule> getModuleList() {
        return moduleList;
    }
}
