package com.piotrak.scenes;

import com.piotrak.Constants;
import com.piotrak.SmartHomeApp;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommunication;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.modularity.rules.RulesGlosniki;
import com.piotrak.impl.modularity.rules.RulesMonitor;
import com.piotrak.impl.types.ConnectivityType;
import com.piotrak.impl.types.ModuleType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {
    
    private static final String CONFIG_FILE = "config.xml";
    
    
    @Test
    public void outsideConfigFileTest() {
        SmartHomeApp.main(new String[]{"-C", "config.xml"});
    }
    //TODO: dodac test parsowania visibility
    
    @Test
    public void oneMQTTModuleTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
        app.loadConfig(configuration);

//        test module
        assertEquals(1, app.getAllModulesList().size());
        Module module = app.getAllModulesList().get(0);
        assertEquals(module.getModuleType(), ModuleType.SWITCH);
        assertEquals("Monitor", module.getName());
        assertEquals("Monitor", module.getDisplayName());
        assertEquals("monitor", module.getIcon());

//        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/test", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));

//        test rules
        assertTrue(module.getRules() instanceof RulesMonitor);

//        test connection service
        assertEquals(1, app.getConnectionServicesList().size());
        assertTrue(app.getConnectionServicesList().get(ConnectivityType.MQTT) instanceof MQTTConnectionService);
        MQTTConnectionService connectionService = (MQTTConnectionService) app.getConnectionServicesList().get(ConnectivityType.MQTT);
        MQTTConnection connection = connectionService.getConnection();
        assertNotNull(connection);
        List<Module> moduleList = connectionService.getModulesList();
        assertEquals(1, moduleList.size());
        moduleList.forEach(m -> assertEquals(m.getCommunication().getConnectivityType(), ConnectivityType.MQTT));

//        test topics map
        Map<String, List<Module>> topicsMap = connectionService.getTopicsMap();
        assertEquals(1, topicsMap.size());
        assertEquals(topicsMap.get("czuj").get(0), module);
        assertNotSame(topicsMap, connectionService.getTopicsMap());

//        test actor
        assertNotNull(connectionService.getActorsService());
        
//        test connection
        assertFalse(connection.isConnected());
    }
    
    @Test
    public void twoSimilarMQTTModulesTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
        Map<String, String> props = new HashMap<>(6);
        props.put("type", "switch");
        props.put("name", "Glosniki");
        props.put("displayName", "Głośniki");
        props.put("icon", "speakers");
        props.put("conType", "MQTT");
        props.put("conSub", "czuj");
        props.put("conPub", "czuj/test");
        addModule(configuration, props, 1);
        app.loadConfig(configuration);
        
        //        test module
        assertEquals(2, app.getAllModulesList().size());
        Module module = app.getAllModulesList().get(0);
        assertEquals(module.getModuleType(), ModuleType.SWITCH);
        assertEquals("Monitor", module.getName());
        assertEquals("Monitor", module.getDisplayName());
        assertEquals("monitor", module.getIcon());
    
        Module module2 = app.getAllModulesList().get(1);
        assertEquals(module2.getModuleType(), ModuleType.SWITCH);
        assertEquals("Glosniki", module2.getName());
        assertEquals("Głośniki", module2.getDisplayName());
        assertEquals("speakers", module2.getIcon());
    
        //        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/test", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));
    
        //        test communication
        assertTrue(module2.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication2 = (MQTTCommunication) module2.getCommunication();
        assertEquals(2, communication2.getCommunicationMap().size());
        assertEquals("czuj", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/test", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));
    
        //        test rules
        assertTrue(module.getRules() instanceof RulesMonitor);
        assertTrue(module2.getRules() instanceof RulesGlosniki);

//        test connection service
        assertEquals(1, app.getConnectionServicesList().size());
        assertTrue(app.getConnectionServicesList().get(ConnectivityType.MQTT) instanceof MQTTConnectionService);
        MQTTConnectionService connectionService = (MQTTConnectionService) app.getConnectionServicesList().get(ConnectivityType.MQTT);
        MQTTConnection connection = connectionService.getConnection();
        assertNotNull(connection);
        List<Module> moduleList = connectionService.getModulesList();
        assertEquals(2, moduleList.size());
        moduleList.forEach(m -> assertEquals(m.getCommunication().getConnectivityType(), ConnectivityType.MQTT));

//        test topics map
        Map<String, List<Module>> topicsMap = connectionService.getTopicsMap();
        assertEquals(1, topicsMap.size());
        assertEquals(topicsMap.get("czuj").size(), 2);
        assertEquals(topicsMap.get("czuj").get(0), module);
        assertEquals(topicsMap.get("czuj").get(1), module2);
        assertNotSame(topicsMap, connectionService.getTopicsMap());

//        test actor
        assertNotNull(connectionService.getActorsService());

//        test connection
        assertFalse(connection.isConnected());
    
    }
    
    @Test
    public void twoDifferentMQTTModulesTest() throws ConfigurationException {
        SmartHomeApp app = new SmartHomeApp();
        XMLConfiguration configuration = new XMLConfiguration(CONFIG_FILE);
        Map<String, String> props = new HashMap<>(6);
        props.put("type", "switch");
        props.put("name", "Glosniki");
        props.put("displayName", "Głośniki");
        props.put("icon", "speakers");
        props.put("conType", "MQTT");
        props.put("conSub", "czuj2");
        props.put("conPub", "czuj2/test");
        addModule(configuration, props, 1);
        app.loadConfig(configuration);
        
        //        test module
        assertEquals(2, app.getAllModulesList().size());
        Module module = app.getAllModulesList().get(0);
        assertEquals(module.getModuleType(), ModuleType.SWITCH);
        assertEquals("Monitor", module.getName());
        assertEquals("Monitor", module.getDisplayName());
        assertEquals("monitor", module.getIcon());
    
        Module module2 = app.getAllModulesList().get(1);
        assertEquals(module.getModuleType(), ModuleType.SWITCH);
        assertEquals("Glosniki", module2.getName());
        assertEquals("Głośniki", module2.getDisplayName());
        assertEquals("speakers", module2.getIcon());

//        test communication
        assertTrue(module.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication = (MQTTCommunication) module.getCommunication();
        assertEquals(2, communication.getCommunicationMap().size());
        assertEquals("czuj", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj/test", communication.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));
        
        assertTrue(module2.getCommunication() instanceof MQTTCommunication);
        MQTTCommunication communication2 = (MQTTCommunication) module2.getCommunication();
        assertEquals(2, communication2.getCommunicationMap().size());
        assertEquals("czuj2", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE));
        assertEquals("czuj2/test", communication2.getCommunicationMap().get(Constants.MQTT_TOPIC_PUBLISH));

//        test rules
        assertTrue(module.getRules() instanceof RulesMonitor);
        assertTrue(module2.getRules() instanceof RulesGlosniki);

//        test connection service
        assertEquals(1, app.getConnectionServicesList().size());
        assertTrue(app.getConnectionServicesList().get(ConnectivityType.MQTT) instanceof MQTTConnectionService);
        MQTTConnectionService connectionService = (MQTTConnectionService) app.getConnectionServicesList().get(ConnectivityType.MQTT);
        MQTTConnection connection = connectionService.getConnection();
        assertNotNull(connection);
        List<Module> moduleList = connectionService.getModulesList();
        assertEquals(2, moduleList.size());
        moduleList.forEach(m -> assertEquals(m.getCommunication().getConnectivityType(), ConnectivityType.MQTT));

//        test topics map
        Map<String, List<Module>> topicsMap = connectionService.getTopicsMap();
        assertEquals(2, topicsMap.size());
        assertEquals(topicsMap.get("czuj").size(), 1);
        assertEquals(topicsMap.get("czuj2").size(), 1);
        assertEquals(topicsMap.get("czuj").get(0), module);
        assertEquals(topicsMap.get("czuj2").get(0), module2);
        assertNotSame(topicsMap, connectionService.getTopicsMap());

//        test actor
        assertNotNull(connectionService.getActorsService());

//        test connection
        assertFalse(connection.isConnected());
    
    }
    
    private void addModule(XMLConfiguration configuration, Map<String, String> props, int pos) {
        configuration.addProperty("modules.module(" + pos + ").type", props.get("type"));
        configuration.addProperty("modules.module(" + pos + ").name", props.get("name"));
        configuration.addProperty("modules.module(" + pos + ").displayName", props.get("displayName"));
        configuration.addProperty("modules.module(" + pos + ").icon", props.get("icon"));
        configuration.addProperty("modules.module(" + pos + ").connection.type", props.get("conType"));
        configuration.addProperty("modules.module(" + pos + ").connection.topic-subscribe", props.get("conSub"));
        configuration.addProperty("modules.module(" + pos + ").connection.topic-publish", props.get("conPub"));
        
    }
    
}
