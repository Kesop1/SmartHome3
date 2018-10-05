package com.piotrak.main;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import static com.piotrak.ServerConstants.CONFIG_FILE;

public class Main {
    
    public static final Logger LOGGER = Logger.getLogger(Main.class);
    
    public static void main(String[] args) {
        try {
            LOGGER.debug("Looking for config file on the classpath");
            XMLConfiguration configFile = new XMLConfiguration(CONFIG_FILE);
            SmartHomeServer smartHomeServer = new SmartHomeServer(configFile);
            if ((smartHomeServer.getServerHandler()) == null) {
                System.exit(0);
            }
            
            smartHomeServer.runServer();
            
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + CONFIG_FILE + "\n", e);
        }
    }
}
