package com.piotrak;

import com.piotrak.elements.ScreenElement;
import com.piotrak.elements.SwitchElement;
import com.piotrak.modularity.ClientModule;
import com.piotrak.types.ModuleType;
import org.apache.commons.configuration.HierarchicalConfiguration;
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
    
    private static List<Element> elementList = new ArrayList<>();
    
    private static Map<String, Screen> screenMap = new HashMap<>();
    
    private VisibilityAppUtils() {
        //do not instantiate
    }
    
    public static void createScreens(HierarchicalConfiguration screensConfig) {
        List<HierarchicalConfiguration> screenConfigList = screensConfig.configurationsAt(CONFIG_SCREEN);
        for (HierarchicalConfiguration screenConfig : screenConfigList) {
            Screen screen = createScreen(screenConfig);
            screenMap.put(screen.getName(), screen);
            LOGGER.info("Screen loaded: " + screen.getTitle());
        }
    }
    
    private static Screen createScreen(HierarchicalConfiguration screenConfig) {
        String name = screenConfig.getString("name") == null ? DEFAULT_SCREEN_NAME : screenConfig.getString("name");
        String title = screenConfig.getString("title") == null ? name : screenConfig.getString("title");
        String icon = screenConfig.getString("icon") == null ? DEFAULT_ICON_SCREEN : screenConfig.getString("icon");
        String background = screenConfig.getString("background") == null ? DEFAULT_SCREEN_BACKGROUND : screenConfig.getString("background");
        return new Screen(name, title, icon, background, new ArrayList<>(0));
    }
    
    public static void createElements(HierarchicalConfiguration screensConfig) {
        List<HierarchicalConfiguration> screenConfigList = screensConfig.configurationsAt(CONFIG_SCREEN);
        for (HierarchicalConfiguration screenConfig : screenConfigList) {
            String screenName = screenConfig.getString("name");
            Screen screen = screenMap.get(screenName);
            if (screen == null) {
                LOGGER.warn("An error occurred when configuring screen " + screenName);
                continue;
            }
            LOGGER.info("Loading elements for the screen: " + screenName);
            List<Element> elementList = createElementsForScreen(screenConfig);
            LOGGER.info("Screen " + screenName + " elements: " + elementList);
            screen.getElementList().addAll(elementList);
        }
    }
    
    private static List<Element> createElementsForScreen(HierarchicalConfiguration screenConfig) {
        List<Element> elementList = new ArrayList<>();
        List<HierarchicalConfiguration> elementConfigList = screenConfig.configurationsAt(CONFIG_ELEMENT);
        for (HierarchicalConfiguration elementConfig : elementConfigList) {
            Element element = createElement(elementConfig);
            if (element != null) {
                elementList.add(element);
            }
        }
        return elementList;
    }
    
    private static Element createElement(HierarchicalConfiguration config) {
        Element element = null;
        int x = config.getInt("[@X]");
        int y = config.getInt("[@Y]");
        HierarchicalConfiguration elementConfig;
        String name;
        if ((name = config.getString("module.name")) != null) {
            elementConfig = config.configurationAt("module");
            searchForExistingElement(name);
            ClientModule module = new ClientModule(elementConfig);
            if (module.getModuleType() == ModuleType.SWITCH) {
                element = new SwitchElement(module, x, y);
                LOGGER.info("Module loaded: " + element.getTitle());
            } else {
                LOGGER.error("Unknown Module type: " + module.getModuleType().name() + ". Unable to create the screen element");
            }
        } else if ((name = config.getString("screen.name")) != null) {
            searchForExistingElement(name);
            Screen screen = screenMap.get(name);
            element = new ScreenElement(screen, x, y);
            LOGGER.info("Screen loaded: " + element.getTitle());
        }
        if (element != null) {
            elementList.add(element);
        }
        return element;
    }
    
    private static Element searchForExistingElement(String name) {
        for (Element existingElement : elementList) {
            if (existingElement.getTitle().equals(name)) {
                return existingElement;
            }
        }
        return null;
    }
    
}
