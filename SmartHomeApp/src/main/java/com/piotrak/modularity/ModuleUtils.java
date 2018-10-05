package com.piotrak.modularity;

import org.apache.log4j.Logger;

public abstract class ModuleUtils {
    
    public static final Logger LOGGER = Logger.getLogger(ModuleUtils.class);
    
    public static final String MODULE_MSG_START = "Module: [";
    
    public static final String MODULE_MSG_NAME = " name=";
    
    public static final String MODULE_MSG_TYPE = " type=";
    
    public static final String MODULE_MSG_END = " ]";
    
    public static String moduleToString(Module module) {
        return MODULE_MSG_START + MODULE_MSG_NAME + module.getName() + MODULE_MSG_TYPE + module.getModuleType() + MODULE_MSG_END;
    }
    
}
