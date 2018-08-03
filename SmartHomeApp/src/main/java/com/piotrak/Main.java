package com.piotrak;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Main {
    
    public static final String CONFIG_FILE = "main/resources/config.xml";
    
    public static void main(String[] args) {
        Main main = new Main();
        try {
            XMLConfiguration config = new XMLConfiguration(CONFIG_FILE);
            List<IConnection> connections = main.setUpConnections(config);
            
        } catch (ConfigurationException e) {
            System.out.println("Problem occurred while reading the config file: " + CONFIG_FILE + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<IConnection> setUpConnections(XMLConfiguration config) {
        List<IConnection> connections = new ArrayList<>(1);
        List<HierarchicalConfiguration> connectionsList = config.configurationsAt("connections.connection");
        for (HierarchicalConfiguration connectionConfig : connectionsList) {
            IConnection connection;
            String className = connectionConfig.getString("classname");
            try {
                connection = (IConnection) Class.forName(className).newInstance();
                connection.config(connectionConfig);
                connections.add(connection);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Connection classname is incorrect: " + className);
                e.printStackTrace();
            } catch (InstantiationException e) {
                System.out.println("Exception occurred while instantiating " + className);
                e.printStackTrace();
            }
            
        }
        return connections;
    }
}
