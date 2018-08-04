package com.piotrak.modularity;

import com.piotrak.modularity.rules.IRules;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public abstract class Module {
    
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(Module.class);
    
    private String name;
    
    private String icon;
    
    private String connection;
    
    private IRules rules;
    
    public void config(HierarchicalConfiguration config) {
        name = config.getString("name") == null ? "" : config.getString("name");
        icon = config.getString("icon") == null ? "" : config.getString("icon");
        connection = config.getString("connection.classname") == null ? "" : config.getString("connection.classname");
        setRules();
    }
    
    public void setRules() {
        if (StringUtils.isEmpty(name)) {
            LOGGER.warn("Module name is empty, unable to set the rules");
            return;
        }
        try {
            rules = (IRules) Class.forName(IRules.class.getName() + name).newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        
    }
}
