package com.piotrak.connectivity;

import com.piotrak.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.connectivity.mqtt.MQTTModuleConnection;
import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.rules.Rules;
import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ConnectionServiceUtils {
    
    public static final Logger LOGGER = Logger.getLogger(ConnectionServiceUtils.class);
    private static final String CONFIG_CONNECTION = "connections.connection";
    
    private ConnectionServiceUtils() {
        //do not instantiate utils class
    }
    
    public static Map<ConnectivityType, IConnectionService> loadConnectionServices(XMLConfiguration configFile, Rules rules, List<ServerModule> moduleList) {
        List<HierarchicalConfiguration> connectionConfigList = configFile.configurationsAt(CONFIG_CONNECTION);
        Map<ConnectivityType, IConnectionService> connectionServicesMap = new EnumMap<>(ConnectivityType.class);
        for (HierarchicalConfiguration connectionConfig : connectionConfigList) {
            IConnectionService connectionService;
            IModuleConnection connection;
            ConnectivityType connectivityType = ConnectivityType.valueOf(connectionConfig.getString("type"));
            if (ConnectivityType.MQTT.equals(connectivityType)) {
                connectionService = new MQTTConnectionService();
                connection = new MQTTModuleConnection();
                ((MQTTModuleConnection) connection).setRules(rules);
            } else {
                LOGGER.error("Connection type is incorrect");
                break;
            }
            connection.config(connectionConfig);
            connectionService.config(moduleList, connection);
            connectionServicesMap.put(connectivityType, connectionService);
            LOGGER.debug("Loaded connectionService: " + connectionService.getConnectivityType());
        }
        return connectionServicesMap;
    }
}
