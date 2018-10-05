package com.piotrak;

import com.piotrak.elements.ScreenElelement;
import com.piotrak.elements.SwitchElement;
import com.piotrak.modularity.ClientModule;
import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisibilityAppUtils {
    
    public static final org.apache.log4j.Logger LOGGER = Logger.getLogger(VisibilityAppUtils.class);
    
    private static final String CONFIG_ELEMENT = "elements.element";
    
    private static final String CONFIG_SCREEN = "screen";
    
    private static final String DEFAULT_ICON_SCREEN = "defaultScreenIcon";
    
    private static final String DEFAULT_SCREEN_NAME = "Screen";
    
    private static final String DEFAULT_SCREEN_BACKGROUND = "defaultBackground";
    
    public static Map<String, Screen> configScreensAndElements(HierarchicalConfiguration screenConfigFile, List<ClientModule> moduleList) {
        List<HierarchicalConfiguration> screenConfigList = screenConfigFile.configurationsAt(CONFIG_SCREEN);
        Map<String, Screen> screenMap = new HashMap<>(0);
        for (HierarchicalConfiguration screenConfig : screenConfigList) {
            Screen screen = createScreenFromConfig(screenConfig, screenMap);
            List<HierarchicalConfiguration> elementConfigList = screenConfig.configurationsAt(CONFIG_ELEMENT);
            List<IElement> elementList = createElementListFromScreenConfig(elementConfigList, moduleList, screenMap);
            screen.getElementList().addAll(elementList);
            screenMap.put(screen.getName(), screen);
        }
        return screenMap;
    }
    
    public static Screen createScreenFromConfig(HierarchicalConfiguration screenConfig, Map<String, Screen> screenMap) {
        String name = screenConfig.getString("name") == null ? DEFAULT_SCREEN_NAME : screenConfig.getString("name");
        String title = screenConfig.getString("title") == null ? name : screenConfig.getString("title");
        String icon = screenConfig.getString("icon") == null ? DEFAULT_ICON_SCREEN : screenConfig.getString("icon");
        String background = screenConfig.getString("background") == null ? DEFAULT_SCREEN_BACKGROUND : screenConfig.getString("background");
        
        if (screenMap.containsKey(name)) {
            Screen screen = screenMap.get(name);
            if (screen.getElementList().isEmpty()) {
                screen.setTitle(title);
                screen.setIcon(icon);
                screen.setBackground(background);
            }
            return screen;
        }
        return new Screen(name, title, icon, background, new ArrayList<>(0));
    }
    
    public static List<IElement> createElementListFromScreenConfig(List<HierarchicalConfiguration> elementConfigList,
                                                                   List<ClientModule> moduleList, Map<String, Screen> screenMap) {
        List<IElement> elementList = new ArrayList<>(0);
        
        for (HierarchicalConfiguration elementConfig : elementConfigList) {
            IElement element = creteElementFromScreenConfig(elementConfig, moduleList, screenMap);
            if (element != null) {
                elementList.add(element);
            }
        }
        return elementList;
    }
    
    public static IElement creteElementFromScreenConfig(HierarchicalConfiguration elementConfig, List<ClientModule> moduleList, Map<String, Screen> screenMap) {
        IElement element = null;
        int x = elementConfig.getInt("[@X]");
        int y = elementConfig.getInt("[@Y]");
        if (elementConfig.getString("module.[@name]") != null) {
            ClientModule module = getClientModuleFromConfig(elementConfig, moduleList);
            if (module == null) {
                return null;
            }
            if (module.getModuleType() == ModuleType.SWITCH) {
                element = new SwitchElement(module, x, y);
            } else {
                LOGGER.error("Unknown Module type: " + module.getModuleType().name() + ". Unable to create the screen element");
            }
        } else if (elementConfig.getString("screen.[@name]") != null) {
            String name = elementConfig.getString("screen.[@name]");
            screenMap.computeIfAbsent(name, k -> new Screen(name, "", "", "", new ArrayList<>(0)));
            /*
            Screen screen = screenMap.get(name);
            //if child screen is added to the parent before it is even created create a Screen shell object and put it on the map
            if (screen == null) {
                screen = new Screen(name, "", "", "", new ArrayList<>(0));
                screenMap.put(name, screen);
            }*/
            element = new ScreenElelement(screenMap.get(name), x, y);
        }
        return element;
    }
    
    private static ClientModule getClientModuleFromConfig(HierarchicalConfiguration config, List<ClientModule> moduleList) {
        String name = config.getString("module.[@name]");
        ClientModule module = null;
        
        //check if the module was defined in the server app config
        for (ClientModule m : moduleList) {
            if (name.equals(m.getName())) {
                module = m;
                break;
            }
        }
        if (module == null) {
            LOGGER.warn("Module " + name + " was not defined in the server app config file");
            return null;
        }
        
        String displayName = config.getString("module.displayName");
        String icon = config.getString("module.icon");
        if (StringUtils.isNotEmpty(displayName)) {
            module.setDisplayName(displayName);
        } else {
            module.setDisplayName(name);
        }
        if (StringUtils.isNotEmpty(icon)) {
            module.setIcon(icon);
        }
        return module;
    }
    
}
