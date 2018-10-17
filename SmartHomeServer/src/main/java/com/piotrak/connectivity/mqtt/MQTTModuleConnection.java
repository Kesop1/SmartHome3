package com.piotrak.connectivity.mqtt;

import com.piotrak.connectivity.Command;
import com.piotrak.connectivity.IModuleConnection;
import com.piotrak.rules.Rules;
import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MQTTModuleConnection implements IModuleConnection {
    
    private static final Logger LOGGER = Logger.getLogger(MQTTModuleConnection.class);
    private static final String SEPARATOR = "://";
    
    private ConnectivityType connectivityType = ConnectivityType.MQTT;
    
    private String host;
    private String port;
    private String protocol;
    private MqttClient mqttClient;
    private String uri = "";
    
    private Rules rules;
    
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
            LOGGER.info("MQTT connection established: " + mqttClient.getServerURI());
        } catch (MqttException e) {
            LOGGER.error("Exception occurred while establishing MQTTModuleConnection", e);
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
                LOGGER.error("Exception occurred while disconnecting MQTTModuleConnection", e);
            }
        }
    }
    
    @Override
    public void sendCommand(Command command) {
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
                    LOGGER.info("MQTTMessage received in topic " + s + ": " + message);
                    MQTTCommand command = new MQTTCommand(s, message);
                    rules.act(command);
                }
                
                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    LOGGER.info("MQTTMessage successfully sent");
                }
            });
        }
    }
    
    public void setRules(Rules rules) {
        this.rules = rules;
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
