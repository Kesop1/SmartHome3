package com.piotrak.connectivity.mqtt;

import com.piotrak.ServerConstants;
import com.piotrak.connectivity.Command;
import com.piotrak.connectivity.IConnectionService;
import com.piotrak.connectivity.IModuleConnection;
import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.types.ConnectivityType;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTTConnectionService implements IConnectionService {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTConnectionService.class);
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private MQTTModuleConnection connection;
    
    private List<ServerModule> modulesList;
    
    private Map<String, List<ServerModule>> topicsMap = new HashMap<>(1);
    
    @Override
    public void config(List<ServerModule> modules, IModuleConnection connection) {
        if (!(connection instanceof MQTTModuleConnection)) {
            LOGGER.error("Invalid connection provided for the MQTTConnectionService");
            return;
        }
        this.modulesList = getModulesPerConnectivityType(modules);
        this.connection = (MQTTModuleConnection) connection;
        loadTopics();
    }
    
    @Override
    public void startService() {
        if (connection != null) {
            connection.connect();
            topicsMap.keySet().forEach(topic -> {
                try {
                    connection.getMqttClient().subscribe(topic);
                } catch (MqttException e) {
                    LOGGER.error("Error while subscribing to topic " + topic, e);
                }
            });
        } else {
            LOGGER.error("Unable to start MQTT service, connection was not set");
        }
    }
    
    private void loadTopics() {
        modulesList.forEach(module -> {
            String topic = module.getCommunication().getCommunicationMap().get(ServerConstants.MQTT_TOPIC_SUBSCRIBE);
            if (!topicsMap.containsKey(topic)) {
                topicsMap.put(topic, new ArrayList<>(1));
            }
            topicsMap.get(topic).add(module);
        });
    }
    
    @Override
    public void actOnCommand(Command command) {
        //all actions are performed in connection.setCallback() method
    }
    
    @Override
    public MQTTModuleConnection getConnection() {
        return connection;
    }
    
    @Override
    public List<ServerModule> getModulesList() {
        return new ArrayList<>(modulesList);
    }
    
    public Map<String, List<ServerModule>> getTopicsMap() {
        Map<String, List<ServerModule>> topicsMapCopy = new HashMap<>(topicsMap.size());
        for (Map.Entry<String, List<ServerModule>> pair : topicsMap.entrySet()) {
            String topic = pair.getKey();
            List<ServerModule> modulesListCopy = new ArrayList<>(topicsMap.get(topic).size());
            modulesListCopy.addAll(pair.getValue());
            topicsMapCopy.put(topic, modulesListCopy);
        }
        return topicsMapCopy;
    }
    
    @Override
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
