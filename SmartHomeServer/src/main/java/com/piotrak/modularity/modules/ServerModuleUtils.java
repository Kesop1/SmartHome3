package com.piotrak.modularity.modules;

import com.piotrak.modularity.ModuleUtils;
import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class ServerModuleUtils extends ModuleUtils {
    
    public static final Logger LOGGER = Logger.getLogger(ServerModuleUtils.class);
    public static final String CONFIG_MODULE = "modules.module";
    
    private ServerModuleUtils() {
        //do not instantiate utils class
    }
    
    public static List<ServerModule> loadModules(XMLConfiguration configFile) {
        List<HierarchicalConfiguration> moduleConfigList = configFile.configurationsAt(CONFIG_MODULE);
        List<ServerModule> moduleList = new ArrayList<>(1);
        for (HierarchicalConfiguration moduleConfig : moduleConfigList) {
            ServerModule module = new ServerModule(moduleConfig);
            moduleList.add(module);
            LOGGER.info("Loaded " + module.toString());
        }
        LOGGER.info(moduleList.size() + " modules loaded");
        return moduleList;
    }
    
    public static List<ServerModule> getModulesByCommunicationType(ConnectivityType connectivityType, List<ServerModule> moduleList) {
        List<ServerModule> modulesForComm = new ArrayList<>(1);
        moduleList.forEach(module -> {
            if (connectivityType.equals(module.getCommunication().getConnectivityType())) {
                modulesForComm.add(module);
            }
        });
        return modulesForComm;
    }
    
    public static String getModuleListAsString(List<ServerModule> moduleList) {
        StringBuilder sb = new StringBuilder(0);
        moduleList.forEach(m -> sb.append(moduleToString(m)).append(", "));
        return sb.toString();
    }
    
}
