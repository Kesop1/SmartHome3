package com.piotrak;

import com.piotrak.contract.connectivity.ActorsService;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartHomeApp {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeApp.class);
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static final String CONFIG_MODULE = "modules.module";
    
    private static final String CONFIG_CONNECTION = "connections.connection";
    
    private List<Module> allModulesList = new ArrayList<>(1);
    
    private Map<ConnectivityType, IConnectionService> connectionServicesList = new HashMap<>(1);
    
    private ActorsService actorsService = new ActorsService();
    
    public static void main(String[] args) {
        String config = CONFIG_FILE;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-C") && args.length > i + 1) {
                    config = args[i + 1];
                }
            }
        }
        try {
            XMLConfiguration configFile = new XMLConfiguration(config);
            SmartHomeApp app = new SmartHomeApp();
            app.loadConfig(configFile);
            ServicesApp servicesApp = new ServicesApp(app.getConnectionServicesList());
            servicesApp.connect();
            VisibilityApp visibilityApp = new VisibilityApp();
//            visibilityApp.runVisibilityApp(args, app.getConnectionServicesList());
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + config + "\n", e);
        }
    }
    
    public void loadConfig(XMLConfiguration config) {
        loadModules(config);
        loadConnectionServices(config);
    }
    
    private void loadConnectionServices(XMLConfiguration config) {
        List<HierarchicalConfiguration> connectionConfigList = config.configurationsAt(CONFIG_CONNECTION);
        for (HierarchicalConfiguration connectionConfig : connectionConfigList) {
            IConnectionService connectionService;
            IConnection connection;
            ConnectivityType connectivityType = ConnectivityType.valueOf(connectionConfig.getString("type"));
            if (ConnectivityType.MQTT.equals(connectivityType)) {
                connectionService = new MQTTConnectionService();
                connection = new MQTTConnection();
            } else {
                LOGGER.error("Connection type is incorrect");
                return;
            }
            connection.config(connectionConfig);
            connectionService.config(getModulesByCommunicationType(connectivityType), connection, actorsService);
            connectionServicesList.put(connectivityType, connectionService);
        }
        actorsService.config(connectionServicesList);
    }
    
    private List<Module> getModulesByCommunicationType(ConnectivityType connectivityType) {
        List<Module> modulesList = new ArrayList<>(1);
        allModulesList.forEach(module -> {
            if (connectivityType.equals(module.getCommunication().getConnectivityType())) {
                modulesList.add(module);
            }
        });
        return modulesList;
    }
    
    private void loadModules(XMLConfiguration config) {
        List<HierarchicalConfiguration> moduleConfigList = config.configurationsAt(CONFIG_MODULE);
        for (HierarchicalConfiguration moduleConfig : moduleConfigList) {
            Module module = new Module();
            module.config(moduleConfig);
            allModulesList.add(module);
        }
    }
    
    
    public List<Module> getAllModulesList() {
        return allModulesList; //TODO security
    }
    
    public Map<ConnectivityType, IConnectionService> getConnectionServicesList() {
        return connectionServicesList; //TODO security
    }
}
