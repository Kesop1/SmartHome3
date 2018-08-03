package com.piotrak.mqtt;

import com.piotrak.ICommand;
import com.piotrak.IConnection;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MQTTConnection implements IConnection {
    
    private String host;
    private String port;
    private String protocol;
    private static final String SEPARATOR = "://";
    private static final Logger LOGGER = Logger.getLogger(MQTTConnection.class);
    private MqttClient mqttClient;
    private String uri = "";
    private List<String> topics = new ArrayList<>(0);
    
    @Override
    public void config(HierarchicalConfiguration config) {
        host = config.getString("host") == null ? "0.0.0.0" : config.getString("host");
        port = config.getString("port") == null ? "0" : config.getString("port");
        protocol = config.getString("protocol") == null ? "tcp" : config.getString("protocol");
        uri = protocol + SEPARATOR + host + ":" + port;
    }
    
    @Override
    public void connect() {
        try {
            mqttClient = new MqttClient(uri, MqttClient.generateClientId(), new MemoryPersistence());
            setCallback();
            mqttClient.connect();
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
            try {
                mqttClient.publish(mqttCommand.getTopic(), mqttCommand.getMessage().getBytes(UTF_8), 2, false);
            } catch (MqttException e) {
                LOGGER.error("Error occurred while publishing MQTT command", e);
            }
        }
    }
    
    @Override
    public void listenForCommand() {
        if (mqttClient != null) {
            for (String topic : topics) {
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
                    LOGGER.debug("Message received in topic " + s + ": " + message);
                    System.out.println("Message received in topic " + s + ": " + message);
                    MQTTCommand command = new MQTTCommand(s, message);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    LOGGER.info("Message successfully sent");
                }
            });
        }
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
    
    public MqttClient getMqttClient() {
        return mqttClient;
    }
    
}
