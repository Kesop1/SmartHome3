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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {
    
    private static final String CONFIG_FILE = "config.xml";
    
    @Test
    public void outsideConfigFileTest() {
        Main.main(new String[]{"-C", "config.xml"});
    }
    
    @Test
    public void oneMQTTModuleTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
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
        assertTrue(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)).contains(module));
        assertNull(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH)));
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void twoSimilarMQTTModulesTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
        Map<String, String> props = new HashMap<>(6);
        props.put("classname", "com.piotrak.modularity.modules.SwitchModule");
        props.put("name", "Monitor");//because no RulesWiatrak file
        props.put("icon", "wiatrak");
        props.put("conType", "MQTT");
        props.put("conSub", "czuj");
        props.put("conPub", "czuj/test");
        addModule(configuration, props, 1);
        app.loadConfig(configuration);
        
        //        test module
        assertEquals(2, app.getModules().size());
        assertTrue(app.getModules().get(0) instanceof SwitchModule);
        SwitchModule module = (SwitchModule) app.getModules().get(0);
        assertEquals("Monitor", module.getName());
        assertEquals("monitor", module.getIcon());
        
        assertTrue(app.getModules().get(1) instanceof SwitchModule);
        SwitchModule module2 = (SwitchModule) app.getModules().get(1);
        assertEquals("Monitor", module2.getName());
        assertEquals("wiatrak", module2.getIcon());

//        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/java", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));
        
        assertTrue(module2.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication2 = (MQTTCommunication) module2.getCommunication();
        assertEquals(2, communication2.getCommunicationMap().size());
        assertEquals("czuj", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/test", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));

//        test rules
        IRules rules = module.getRules();
        assertTrue(rules instanceof RulesMonitor);
        
        IRules rules2 = module2.getRules();
        assertTrue(rules2 instanceof RulesMonitor);

//        test connection
        assertTrue(app.getConnections().get(0) instanceof MQTTConnection);
        MQTTConnection connection = (MQTTConnection) app.getConnections().get(0);
        assertEquals(1, connection.getTopicsMap().size());
        assertTrue(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)).contains(module));
        assertTrue(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)).contains(module2));
        assertNull(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH)));
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void twoDifferentMQTTModulesTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
        Map<String, String> props = new HashMap<>(6);
        props.put("classname", "com.piotrak.modularity.modules.SwitchModule");
        props.put("name", "Monitor"); //because no RulesWiatrak file
        props.put("icon", "wiatrak");
        props.put("conType", "MQTT");
        props.put("conSub", "czuj2");
        props.put("conPub", "czuj2/test");
        addModule(configuration, props, 1);
        app.loadConfig(configuration);
        
        //        test module
        assertEquals(2, app.getModules().size());
        assertTrue(app.getModules().get(0) instanceof SwitchModule);
        SwitchModule module = (SwitchModule) app.getModules().get(0);
        assertEquals("Monitor", module.getName());
        assertEquals("monitor", module.getIcon());
        
        assertTrue(app.getModules().get(1) instanceof SwitchModule);
        SwitchModule module2 = (SwitchModule) app.getModules().get(1);
        assertEquals("Monitor", module2.getName());
        assertEquals("wiatrak", module2.getIcon());

//        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/java", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));
        
        assertTrue(module2.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication2 = (MQTTCommunication) module2.getCommunication();
        assertEquals(2, communication2.getCommunicationMap().size());
        assertEquals("czuj2", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj2/test", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));

//        test rules
        IRules rules = module.getRules();
        assertTrue(rules instanceof RulesMonitor);
        
        IRules rules2 = module2.getRules();
        assertTrue(rules2 instanceof RulesMonitor);

//        test connection
        assertTrue(app.getConnections().get(0) instanceof MQTTConnection);
        MQTTConnection connection = (MQTTConnection) app.getConnections().get(0);
        assertEquals(2, connection.getTopicsMap().size());
        assertTrue(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)).contains(module));
        assertTrue(connection.getTopicsMap().get(communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE)).contains(module2));
        assertNull(connection.getTopicsMap().get(communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH)));
        assertNull(connection.getTopicsMap().get(communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH)));
        assertFalse(connection.isConnected());
    }
    
    private void addModule(XMLConfiguration configuration, Map<String, String> props, int pos) {
        configuration.addProperty("modules.module(" + pos + ").classname", props.get("classname"));
        configuration.addProperty("modules.module(" + pos + ").name", props.get("name"));
        configuration.addProperty("modules.module(" + pos + ").icon", props.get("icon"));
        configuration.addProperty("modules.module(" + pos + ").connection.type", props.get("conType"));
        configuration.addProperty("modules.module(" + pos + ").connection.topic-subscribe", props.get("conSub"));
        configuration.addProperty("modules.module(" + pos + ").connection.topic-publish", props.get("conPub"));
        
    }
}
