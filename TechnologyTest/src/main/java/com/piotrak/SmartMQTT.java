package com.piotrak;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * https://www.hivemq.com/blog/mqtt-client-library-encyclopedia-eclipse-paho-java
 */
public class SmartMQTT {

    public static final String HOST = "192.168.1.103";
    public static final String PORT = "1883";
    public static final String PROTOCOL = "tcp://";

    private MqttClient mqttClient;

    public SmartMQTT() {
        String uri = PROTOCOL + HOST + ":" + PORT;
        try {
            mqttClient = new MqttClient(uri, MqttClient.generateClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setCallback() {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                System.out.println("Client disocnnected");
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                System.out.println(s + ": " + new String(mqttMessage.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                System.out.println("Delivery complete");
            }
        });
    }

    public MqttClient getMqttClient() {
        return mqttClient;
    }
}
