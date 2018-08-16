package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ActorsService;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTTConnectionService implements IConnectionService {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTConnectionService.class);
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private MQTTConnection connection;
    
    private List<Module> modulesList;
    
    private Map<String, List<Module>> topicsMap = new HashMap<>(1);
    
    private ActorsService actorsService = null;
    
    @Override
    public void config(List<Module> modules, IConnection connection, ActorsService actorsService) {
        if (!(connection instanceof MQTTConnection)) {
            LOGGER.error("Invalid connection provided for the MQTTConnectionService");
            return;
        }
        setModulesList(modules);
        setConnection((MQTTConnection) connection);
        loadTopics();
        this.actorsService = actorsService;
    }
    
    @Override
    public void startService() {
        if (connection != null) {
            connection.connect(actorsService);
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
            String topic = module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE);
            if (!topicsMap.containsKey(topic)) {
                topicsMap.put(topic, new ArrayList<>(1));
            }
            topicsMap.get(topic).add(module);
        });
    }
    
    @Override
    public void actOnCommand(ICommand command) {
        //all actions are performed in connection.setCallback() method
    }
    
    @Override
    public MQTTConnection getConnection() {
        return connection;
    }
    
    private void setConnection(MQTTConnection connection) {
        this.connection = connection;
    }
    
    @Override
    public List<Module> getModulesList() {
        return modulesList;
    }
    
    private void setModulesList(List<Module> modulesList) {
        this.modulesList = modulesList;
    }
    
    @Override
    public ActorsService getActorsService() {
        return actorsService;
    }
    
    public Map<String, List<Module>> getTopicsMap() {
        Map<String, List<Module>> topicsMapCopy = new HashMap<>(topicsMap.size());
        for (Map.Entry<String, List<Module>> pair : topicsMap.entrySet()) {
            String topic = pair.getKey();
            List<Module> modulesListCopy = new ArrayList<>(topicsMap.get(topic).size());
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
