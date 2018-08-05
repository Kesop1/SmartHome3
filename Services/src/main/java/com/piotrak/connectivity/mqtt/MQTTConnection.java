package com.piotrak.connectivity.mqtt;

import com.piotrak.Constants;
import com.piotrak.connectivity.ICommand;
import com.piotrak.connectivity.IConnection;
import com.piotrak.modularity.modules.Module;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MQTTConnection implements IConnection {
    
    private String host;
    private String port;
    private String protocol;
    private static final String SEPARATOR = "://";
    private static final Logger LOGGER = Logger.getLogger(MQTTConnection.class);
    private MqttClient mqttClient;
    private String uri = "";
    private Map<String, Module> topicsMap;
    
    @Override
    public void config(HierarchicalConfiguration config, List<Module> moduleList) {
        host = config.getString("host") == null ? "0.0.0.0" : config.getString("host");
        port = config.getString("port") == null ? "0" : config.getString("port");
        protocol = config.getString("protocol") == null ? "tcp" : config.getString("protocol");
        uri = protocol + SEPARATOR + host + ":" + port;
        topicsMap = loadTopics(moduleList);
    }
    
    @Override
    public void connect() {
        try {
            mqttClient = new MqttClient(uri, MqttClient.generateClientId(), new MemoryPersistence());
            setCallback();
            mqttClient.connect();
            listenForCommand();
        } catch (MqttException e) {
            LOGGER.error("Exception occurred while establishing MQTTConnection", e);
        }
    }
    
    @Override
    public boolean isConnected() {
        if (mqttClient != null) {
            return mqttClient.isConnected();
        }
        return false;
    }
    
    @Override
    public void disconnect() {
        if (isConnected()) {
            try {
                mqttClient.disconnect();
            } catch (MqttException e) {
                LOGGER.error("Exception occurred while disconnecting MQTTConnection", e);
            }
        }
    }
    
    @Override
    public void sendCommand(ICommand command) {
        if (command instanceof MQTTCommand) {
            MQTTCommand mqttCommand = (MQTTCommand) command;
            MqttTopic mqttTopic = mqttClient.getTopic(mqttCommand.getTopic());
            try {
                mqttTopic.publish(new MqttMessage(mqttCommand.getMessage().getBytes()));
            } catch (MqttException e) {
                LOGGER.error("Error occurred while publishing MQTT command", e);
            }
        }
    }
    
    @Override
    public void listenForCommand() {
        if (isConnected()) {
            for (String topic : getTopicsMap().keySet()) {
                try {
                    mqttClient.subscribe(topic);
                } catch (MqttException e) {
                    LOGGER.error("Error while subscribing to topic " + topic, e);
                }
            }
        }
    }
    
    private void setCallback() {
        if (mqttClient != null) {
            mqttClient.setCallback(new MqttCallback() {
    
                @Override
                public void connectionLost(Throwable throwable) {
                    LOGGER.error("MQTTClient got disconnected");
                }
                
                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    String message = new String(mqttMessage.getPayload());
                    LOGGER.info("Message received in topic " + s + ": " + message);
                    MQTTCommand command = new MQTTCommand(s, message);
                    useRules(command);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    LOGGER.info("Message successfully sent");
                }
            });
        }
    }
    
    private void useRules(MQTTCommand command) {
        for (String topic : getTopicsMap().keySet()) {
            if (topic.equals(command.getTopic())) {
                Module module = getTopicsMap().get(topic);
                module.getRules().useRules(command, module, this);
            }
        }
    }
    
    private Map<String, Module> loadTopics(List<Module> moduleList) {
        Map<String, Module> topics = new HashMap<>(0);
        if (moduleList != null) {
            for (Module module : moduleList) {
                topics.put(module.getCommunication().getCommunication().get(Constants.MQTT_TOPIC_SUBSCRIBE), module);
            }
        } else {
            LOGGER.warn("Modules list is empty, unable to load topics");
        }
        return topics;
    }
    
    public String getHost() {
        return host;
    }
    
    public String getPort() {
        return port;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public Map<String, Module> getTopicsMap() {
        return topicsMap;
    }
}
