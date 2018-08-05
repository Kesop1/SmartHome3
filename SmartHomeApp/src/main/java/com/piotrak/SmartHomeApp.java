package com.piotrak;

import com.piotrak.connectivity.IConnection;
import com.piotrak.modularity.modules.Module;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class SmartHomeApp {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeApp.class);
    
    private List<Module> modules = new ArrayList<>(1);
    
    private List<IConnection> connections = new ArrayList<>(1);
    
    public void loadConfig(String configFilePatk) {
        try {
            XMLConfiguration config = new XMLConfiguration(configFilePatk);
            loadModules(config);
            loadConnections(config);
            
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + configFilePatk + "\n", e);
        }
    }
    
    public void connect() {
        for (IConnection connection : connections) {
            connection.connect();
        }
    }
    
    private void loadModules(XMLConfiguration config) {
        List<HierarchicalConfiguration> modulesListConfig = config.configurationsAt("modules.module");
        for (HierarchicalConfiguration moduleConfig : modulesListConfig) {
            String className = moduleConfig.getString("classname");
            try {
                Module module = (Module) Class.forName(className).newInstance();
                module.config(moduleConfig);
                modules.add(module);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                LOGGER.error("Could not instantiate module " + className, e);
            }
        }
    }
    
    private List<IConnection> loadConnections(XMLConfiguration config) {
        List<HierarchicalConfiguration> connectionsList = config.configurationsAt("connections.connection");
        for (HierarchicalConfiguration connectionConfig : connectionsList) {
            IConnection connection;
            String className = connectionConfig.getString("classname");
            try {
                connection = (IConnection) Class.forName(className).newInstance();
                connection.config(connectionConfig, modules);
                connections.add(connection);
            } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
                LOGGER.error("Cannot instantiate connection " + className, e);
            }
        }
        return connections;
    }
    
    public List<Module> getModules() {
        return modules;
    }
    
    public List<IConnection> getConnections() {
        return connections;
    }
}
