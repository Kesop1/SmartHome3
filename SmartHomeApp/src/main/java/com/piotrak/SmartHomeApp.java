package com.piotrak;

import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartHomeApp {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeApp.class);
    
    public static final String CONFIG_MODULE = "modules.module";
    
    private static final String CONFIG_CONNECTION = "connections.connection";
    
    private List<Module> modules = new ArrayList<>(1);
    
    private Map<String, IConnectionService> connectionServiceList = new HashMap<>(1);
    
    public void loadConfig(XMLConfiguration config) {
        loadModules(config);
        loadConnectionServices(config);
    }
    
    public void connect() {
        for (IConnectionService connectionService : connectionServiceList.values()) {
            connectionService.startService();
        }
    }
    
    private void loadConnectionServices(XMLConfiguration config) {
        List<HierarchicalConfiguration> connectionConfigList = config.configurationsAt(CONFIG_CONNECTION);
        for (HierarchicalConfiguration connectionConfig : connectionConfigList) {
            IConnectionService connectionService;
            IConnection connection;
            String type = connectionConfig.getString("type");
            if (Constants.MQTT.equals(type)) {
                connectionService = new MQTTConnectionService();
                connection = new MQTTConnection();
            } else {
                LOGGER.error("Connection type is incorrect");
                return;
            }
            connection.config(connectionConfig);
            connectionService.config(getModulesByType(type), connection);
            connectionServiceList.put(type, connectionService);
        }
    }
    
    private List<Module> getModulesByType(String type) {
        List<Module> modulesList = new ArrayList<>(1);
        modules.forEach(module -> {
            if (type.equals(module.getCommunicationType())) {
                modulesList.add(module);
            }
        });
        return modulesList;
    }
    
    private void loadModules(XMLConfiguration config) {
        List<HierarchicalConfiguration> moduleConfigList = config.configurationsAt(CONFIG_MODULE);
        for (HierarchicalConfiguration moduleConfig : moduleConfigList) {
            String className = moduleConfig.getString("classname");
            try {
                Module module = (Module) Class.forName(className).newInstance();
                module.config(moduleConfig);
                modules.add(module);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOGGER.error("Could not instantiate module " + className, e);
            }
        }
    }
    
    public List<Module> getModules() {
        return modules; //TODO security
    }
    
    public Map<String, IConnectionService> getConnectionServiceList() {
        return connectionServiceList; //TODO security
    }
}
