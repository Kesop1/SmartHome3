package com.piotrak.modularity;

import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

public abstract class Module {
    
    public static final Logger LOGGER = Logger.getLogger(Module.class);
    
    private ModuleType moduleType;
    
    private String name;
    
    public Module(HierarchicalConfiguration config) {
        name = config.getString("name") == null ? "" : config.getString("name");
        moduleType = ModuleType.valueOf(config.getString("type").toUpperCase());
    }
    
    public Module(ModuleType moduleType, String name) {
        this.moduleType = moduleType;
        this.name = name;
    }
    
    public ModuleType getModuleType() {
        return moduleType;
    }
    
    public String getName() {
        return name;
    }
    
}
