package com.piotrak.main;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import static com.piotrak.ClientConstants.CONFIG_FILE;

public class Main {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            LOGGER.debug("Looking for config file on the classpath");
            XMLConfiguration configFile = new XMLConfiguration(CONFIG_FILE);
            SmartHomeClient smartHomeClient = new SmartHomeClient(configFile);
            smartHomeClient.run();
            
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + CONFIG_FILE + "\n", e);
        }
    }
}
