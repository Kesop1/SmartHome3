package com.piotrak.test.mqtt;

import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MQTTConnectionTest {
    
    public static final String CONFIG_FILE = "config.xml";
    
    @Test
    public void correctConfigTest() {
        MQTTConnection connection = new MQTTConnection();
        connection.config(getSubconfig());
        assertEquals(connection.getHost(), "192.168.1.103");
        assertEquals(connection.getPort(), "1883");
        assertEquals(connection.getProtocol(), "tcp");
    }
    
    @Test
    public void defaultConfigTest() {
        MQTTConnection connection = new MQTTConnection();
        HierarchicalConfiguration connectionConfig = getSubconfig();
        connectionConfig.clearProperty("host");
        connectionConfig.clearProperty("port");
        connectionConfig.clearProperty("protocol");
        connection.config(connectionConfig);
        assertEquals(connection.getHost(), "0.0.0.0");
        assertEquals(connection.getPort(), "0");
        assertEquals(connection.getProtocol(), "tcp");
    }
    
    private HierarchicalConfiguration getSubconfig() {
        try {
            XMLConfiguration config = new XMLConfiguration(CONFIG_FILE);
            HierarchicalConfiguration connectionConfig = config.configurationAt("connections.connection(0)");
            return connectionConfig;
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
