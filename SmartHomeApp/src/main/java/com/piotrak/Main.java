package com.piotrak;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

public class Main {
    
    public static final Logger LOGGER = Logger.getLogger(Main.class);
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static void main(String[] args) {
        SmartHomeApp app = new SmartHomeApp();
        String config = CONFIG_FILE;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-C") && args.length > i + 1) {
                    config = args[i + 1];
                }
            }
        }
        try {
            XMLConfiguration configFile = new XMLConfiguration(config);
            app.loadConfig(configFile);
            app.connect();
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + config + "\n", e);
        }
    
    }
}
