package com.piotrak.modularity.modules;

import com.piotrak.connectivity.IModuleCommunication;
import com.piotrak.connectivity.mqtt.MQTTModuleCommunication;
import com.piotrak.modularity.Module;
import com.piotrak.types.ConnectivityType;
import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

public class ServerModule extends Module {
    
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(ServerModule.class);
    
    private IModuleCommunication communication;
    
    public ServerModule(HierarchicalConfiguration config) {
        super(config);
        setCommunication(config);
    }
    
    public ServerModule(ModuleType moduleType, String name, IModuleCommunication communication) {
        super(moduleType, name);
        this.communication = communication;
    }
    
    public IModuleCommunication getCommunication() {
        return communication;
    }
    
    private void setCommunication(HierarchicalConfiguration config) {
        try {
            ConnectivityType connectionType = Enum.valueOf(ConnectivityType.class, config.getString("connection.type"));
            HierarchicalConfiguration connectionsList = config.configurationAt("connection");
            if (ConnectivityType.MQTT == connectionType) {
                communication = new MQTTModuleCommunication();
                communication.setCommunicationMap(connectionsList);
            } else {
                LOGGER.warn("Unable to assign a communication type " + connectionType);
            }
        } catch (IllegalArgumentException e) {
            LOGGER.error(e.getMessage());
        }
    }
    
}