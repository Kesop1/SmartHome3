package com.piotrak.contract.modularity.modules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommunication;
import com.piotrak.contract.modularity.rules.IRules;
import com.piotrak.impl.connectivity.mqtt.MQTTCommunication;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Module {
    
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Module.class);
    
    private static final String CONFIG_VISIBILITY = "visibility.screen";
    
    private static final String POSX = "posX";
    
    private static final String POSY = "posY";
    
    private String name;
    
    private String displayName;
    
    private String icon;
    
    private IRules rules;
    
    private ICommunication communication;
    
    private String communicationType;
    
    private Map<String, Map<String, Integer>> visibility = new HashMap<>(1);
    
    public void config(HierarchicalConfiguration config) {
        name = config.getString("name") == null ? "" : config.getString("name");
        displayName = config.getString("displayName") == null ? name : config.getString("displayName");
        icon = config.getString("icon") == null ? "" : config.getString("icon");
        setRules();
        setCommunication(config);
        setVisibility(config);
    }
    
    private void setRules() {
        if (StringUtils.isEmpty(name)) {
            LOGGER.warn("Module name is empty, unable to set the rules");
            return;
        }
        try {
            String packageName = IRules.class.getPackage().getName().replace("contract", "impl");
            rules = (IRules) Class.forName(packageName + "." + Constants.RULES + name).newInstance();
        } catch (ClassNotFoundException e) {
            LOGGER.warn("Could not locate rules file for " + this.getClass().getName() + " with name " + name, e);
        } catch (IllegalAccessException | InstantiationException e) {
            LOGGER.error("Could not instantiate class " + this.getClass().getName() + "_" + name, e);
        }
    }
    
    private void setCommunication(HierarchicalConfiguration config) {
        String connectionType = config.getString("connection.type");
        communicationType = connectionType;
        HierarchicalConfiguration connectionsList = config.configurationAt("connection");
        if (Constants.MQTT.equals(connectionType)) {
            communication = new MQTTCommunication();
            communication.setCommunicationMap(connectionsList);
        } else {
            LOGGER.warn("Unable to assign a communication type " + connectionType);
        }
    }
    
    public Map<String, Map<String, Integer>> getVisibility() {
        return visibility;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public IRules getRules() {
        return rules;
    }
    
    public ICommunication getCommunication() {
        return communication;
    }
    
    public String getCommunicationType() {
        return communicationType;
    }
    
    private void setVisibility(HierarchicalConfiguration config) {
        List<HierarchicalConfiguration> screens =
                config.configurationsAt(CONFIG_VISIBILITY);
        screens.forEach(screen -> {
            String visName = screen.getString("[@name]");
            Integer posX = screen.getInt("[@" + POSX + "]");
            Integer posY = screen.getInt("[@" + POSY + "]");
            Map<String, Integer> positions = new HashMap<>(2);
            positions.put(POSX, posX);
            positions.put(POSY, posY);
            visibility.put(visName, positions);
        });
    }
}