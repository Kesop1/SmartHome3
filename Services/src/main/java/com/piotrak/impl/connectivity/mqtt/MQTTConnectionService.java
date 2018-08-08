package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.modularity.mqtt.actors.MQTTActor;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTTConnectionService implements IConnectionService {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTConnectionService.class);
    
    private MQTTConnection connection;
    
    private List<Module> modulesList;
    
    private Map<String, List<Module>> topicsMap = new HashMap<>(1);
    
    private MQTTActor actor = null;
    
    @Override
    public void config(List<Module> modules, IConnection connection) {
        if (!(connection instanceof MQTTConnection)) {
            LOGGER.error("Invalid connection provided for the MQTTConnectionService");
            return;
        }
        setModulesList(modules);
        setConnection((MQTTConnection) connection);
        loadTopics();
        actor = new MQTTActor(getTopicsMap());
    }
    
    @Override
    public void startService() {
        if (connection != null) {
            connection.connect(actor);
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
    public MQTTActor getActor() {
        return actor;
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
    
}
