package com.piotrak.main;

import com.piotrak.Constants;
import com.piotrak.Main;
import com.piotrak.SmartHomeApp;
import com.piotrak.connectivity.mqtt.MQTTCommunication;
import com.piotrak.connectivity.mqtt.MQTTConnection;
import com.piotrak.modularity.modules.SwitchModule;
import com.piotrak.modularity.rules.IRules;
import com.piotrak.modularity.rules.RulesMonitor;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {
    
    @Test
    public void outsideConfigFileTest() {
        Main.main(new String[]{"-C", "config.xml"});
    }
    
    @Test
    public void oneMQTTModuleTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration("config.xml");
        app.loadConfig(configuration);

//        test module
        assertEquals(1, app.getModules().size());
        assertTrue(app.getModules().get(0) instanceof SwitchModule);
        SwitchModule module = (SwitchModule) app.getModules().get(0);
        assertEquals("Monitor", module.getName());
        assertEquals("monitor", module.getIcon());

//        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/java", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));

//        test rules
        IRules rules = module.getRules();
        assertTrue(rules instanceof RulesMonitor);

//        test connection
        assertTrue(app.getConnections().get(0) instanceof MQTTConnection);
        MQTTConnection connection = (MQTTConnection) app.getConnections().get(0);
        assertEquals(1, connection.getTopicsMap().size());
        assertEquals(module, connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)));
        assertNull(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH)));
        assertFalse(connection.isConnected());
    }
}
