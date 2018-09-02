package com.piotrak;

import com.piotrak.types.ModuleType;
import org.apache.commons.lang.StringUtils;

public class Module {
    
    private ModuleType moduleType;
    
    private String name;
    
    private String displayName;
    
    private String icon;
    
    public Module(ModuleType moduleType, String name, String displayName, String icon) throws IllegalArgumentException {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Module name cannot be empty!");
        }
        this.moduleType = moduleType == null ? ModuleType.SWITCH : moduleType;
        this.name = name;
        this.displayName = StringUtils.isEmpty(displayName) ? name : displayName;
        this.icon = StringUtils.isEmpty(icon) ? "defaultIcon" : icon;
    }
    
    public ModuleType getModuleType() {
        return moduleType;
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
}
