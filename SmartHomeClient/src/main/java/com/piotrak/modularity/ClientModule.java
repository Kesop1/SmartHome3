package com.piotrak.modularity;

import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;

import static com.piotrak.modularity.ClientModuleUtils.DEFAULT_ICON;

public class ClientModule extends Module {
    
    private String displayName;
    
    private String icon;
    
    public ClientModule(HierarchicalConfiguration config) {
        super(config);
        displayName = config.getString("displayName") == null ? this.getName() : config.getString("displayName");
        icon = config.getString("icon") == null ? DEFAULT_ICON : config.getString("icon");
    }
    
    public ClientModule(ModuleType moduleType, String name, String displayName, String icon) {
        super(moduleType, name);
        this.displayName = displayName;
        this.icon = icon;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
