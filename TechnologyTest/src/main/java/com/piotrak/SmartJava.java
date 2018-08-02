package com.piotrak;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SmartJava {

    public static void main(String[] args) {

        System.out.println("Startujemy!!!");
        SmartMQTT smartMQTT = new SmartMQTT();
        smartMQTT.setCallback();
        MqttClient mqttClient = smartMQTT.getMqttClient();
        try {
            System.out.println("Attempting connection...");
            mqttClient.connect();
            System.out.println("Successfull");
            System.out.println("Connection check: " + mqttClient.isConnected());
            mqttClient.publish("czuj", "Greetings from Java".getBytes(UTF_8), 2, false);
            mqttClient.subscribe("czuj");

        } catch (MqttException e) {
            e.printStackTrace();
        }


    }
}
