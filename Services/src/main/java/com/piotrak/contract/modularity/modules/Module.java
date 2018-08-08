package com.piotrak.contract.modularity.modules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommunication;
import com.piotrak.contract.modularity.rules.IRules;
import com.piotrak.impl.connectivity.mqtt.MQTTCommunication;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class Module {
    
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Module.class);
    
    private String name;
    
    private String displayName;
    
    private String icon;
    
    private IRules rules;
    
    private ICommunication communication;
    
    private String communicationType;
    
    public void config(HierarchicalConfiguration config) {
        name = config.getString("name") == null ? "" : config.getString("name");
        displayName = config.getString("displayName") == null ? name : config.getString("displayName");
        icon = config.getString("icon") == null ? "" : config.getString("icon");
        setRules();
        setCommunication(config);
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
    
}