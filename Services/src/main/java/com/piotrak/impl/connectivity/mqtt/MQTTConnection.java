package com.piotrak.impl.connectivity.mqtt;

import com.piotrak.contract.connectivity.ActorsService;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTConnection implements IConnection {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTConnection.class);
    private static final String SEPARATOR = "://";
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private String host;
    private String port;
    private String protocol;
    private MqttClient mqttClient;
    private String uri = "";
    
    @Override
    public void config(HierarchicalConfiguration config) {
        host = config.getString("host") == null ? "0.0.0.0" : config.getString("host");
        port = config.getString("port") == null ? "0" : config.getString("port");
        protocol = config.getString("protocol") == null ? "tcp" : config.getString("protocol");
        uri = protocol + SEPARATOR + host + ":" + port;
    }
    
    @Override
    public void connect(ActorsService actorsService) {
        try {
            mqttClient = new MqttClient(uri, MqttClient.generateClientId(), new MemoryPersistence());
            setCallback(actorsService);
            mqttClient.connect();
            LOGGER.info("MQTT connection established");
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
    
    private void setCallback(ActorsService actorsService) {
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
                    actorsService.commandReceived(command);
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
    
    public ConnectivityType getConnectivityType() {
        return connectivityType;
    }
}
