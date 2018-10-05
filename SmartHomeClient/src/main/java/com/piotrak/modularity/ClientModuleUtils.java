package com.piotrak.modularity;

import com.piotrak.types.ModuleType;

import java.util.ArrayList;
import java.util.List;

public class ClientModuleUtils extends ModuleUtils {
    
    public static final String DEFAULT_ICON = "defaultIcon";
    
    private ClientModuleUtils() {
        //do not instantiate utils class
    }
    
    public static ClientModule stringToClientModule(String text) {
        if (!text.startsWith(MODULE_MSG_START)) {
            LOGGER.warn("Unable to parse module from text: " + text);
            return null;
        }
        String name = text.substring(text.indexOf(MODULE_MSG_NAME) + MODULE_MSG_NAME.length(), text.indexOf(MODULE_MSG_TYPE));
        ModuleType type = Enum.valueOf(ModuleType.class, text.substring(text.indexOf(MODULE_MSG_TYPE) + MODULE_MSG_TYPE.length(),
                text.indexOf(MODULE_MSG_END)));
        return new ClientModule(type, name, name, DEFAULT_ICON);
    }
    
    public static List<ClientModule> getClientModuleListFromServer(String message) {
        List<ClientModule> moduleList = new ArrayList<>(0);
        List<String> stringModuleList = new ArrayList<>(0);
        
        do {
            String module = message.substring(message.indexOf(MODULE_MSG_START), message.indexOf(MODULE_MSG_END) + MODULE_MSG_END.length());
            stringModuleList.add(module);
            message = message.substring(message.indexOf(MODULE_MSG_END) + MODULE_MSG_END.length());
        } while (message.contains(MODULE_MSG_START));
        
        for (String m : stringModuleList) {
            ClientModule module = stringToClientModule(m);
            if (module != null) {
                moduleList.add(module);
            }
        }
        if (moduleList.isEmpty()) {
            LOGGER.warn("Error occurred while retrieving the module list from the server");
        }
        
        return moduleList;
    }
    
}
