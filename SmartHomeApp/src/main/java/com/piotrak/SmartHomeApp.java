package com.piotrak;

import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.modularity.rules.Rules;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SmartHomeApp {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeApp.class);
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static final String CONFIG_MODULE = "modules.module";
    
    private static final String CONFIG_CONNECTION = "connections.connection";
    
    private static final String CONFIG_SCREEN = "visibility.screens.screen";
    
    private List<Module> moduleList = new ArrayList<>(1);
    
    private Map<ConnectivityType, IConnectionService> connectionServicesList = new EnumMap<>(ConnectivityType.class);
    
    private Rules rules;
    
    public SmartHomeApp(List<HierarchicalConfiguration> moduleConfigList, List<HierarchicalConfiguration> connectionConfigList) {
        rules = new Rules(connectionServicesList, moduleList);
        loadModules(moduleConfigList);
        loadConnectionServices(connectionConfigList);
    
    }
    
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
            List<HierarchicalConfiguration> moduleConfigList = configFile.configurationsAt(CONFIG_MODULE);
            List<HierarchicalConfiguration> connectionConfigList = configFile.configurationsAt(CONFIG_CONNECTION);
            List<HierarchicalConfiguration> screenConfigList = configFile.configurationsAt(CONFIG_SCREEN);
            SmartHomeApp app = new SmartHomeApp(moduleConfigList, connectionConfigList);
            ServicesApp servicesApp = new ServicesApp(app.getConnectionServicesList());
            servicesApp.connect();
            VisibilityApp visibilityApp = new VisibilityApp();
//            visibilityApp.setActorsService(app.actorsService);
            visibilityApp.config(screenConfigList, app.getModuleList(), app.rules);
            visibilityApp.runVisibilityApp(args, app.getConnectionServicesList());
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + config + "\n", e);
        }
    }
    
    private void loadConnectionServices(List<HierarchicalConfiguration> connectionConfigList) {
        for (HierarchicalConfiguration connectionConfig : connectionConfigList) {
            IConnectionService connectionService;
            IConnection connection;
            ConnectivityType connectivityType = ConnectivityType.valueOf(connectionConfig.getString("type"));
            if (ConnectivityType.MQTT.equals(connectivityType)) {
                connectionService = new MQTTConnectionService();
                connection = new MQTTConnection();
                ((MQTTConnection) connection).setRules(rules);
            } else {
                LOGGER.error("Connection type is incorrect");
                return;
            }
            connection.config(connectionConfig);
            connectionService.config(getModulesByCommunicationType(connectivityType), connection);
            connectionServicesList.put(connectivityType, connectionService);
        }
    }
    
    private List<Module> getModulesByCommunicationType(ConnectivityType connectivityType) {
        List<Module> modulesList = new ArrayList<>(1);
        moduleList.forEach(module -> {
            if (connectivityType.equals(module.getCommunication().getConnectivityType())) {
                modulesList.add(module);
            }
        });
        return modulesList;
    }
    
    private void loadModules(List<HierarchicalConfiguration> moduleConfigList) {
        for (HierarchicalConfiguration moduleConfig : moduleConfigList) {
            Module module = new Module();
            module.config(moduleConfig);
            moduleList.add(module);
        }
    }
    
    public List<Module> getModuleList() {
        return new ArrayList<>(moduleList);
    }
    
    public Map<ConnectivityType, IConnectionService> getConnectionServicesList() {
        return new EnumMap<>(connectionServicesList);
    }
}
