package com.piotrak.modularity;

import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public abstract class Module {
    
    public static final Logger LOGGER = Logger.getLogger(Module.class);
    
    private ModuleType moduleType;
    
    private String name;
    
    public Module(HierarchicalConfiguration config) {
        name = config.getString("name") == null ? "" : config.getString("name");
        String type = config.getString("type");
        if (StringUtils.isNotEmpty(type)) {
            moduleType = ModuleType.valueOf(type.toUpperCase());
        }
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
