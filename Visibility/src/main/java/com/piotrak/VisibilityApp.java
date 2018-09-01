package com.piotrak;

import com.piotrak.contract.connectivity.ActorsService;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.elements.ScreenElelement;
import com.piotrak.elements.SwitchElement;
import com.piotrak.impl.types.ConnectivityType;
import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisibilityApp extends Application {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VisibilityApp.class);
    
    public static final String MAIN = "Main";
    
    public static final String CONFIG_ELEMENT = "elements.element";
    
    private static Map<String, Screen> screenHashMap = new HashMap<>(1);
    
    private List<Module> allModulesList;
    
    private ActorsService actorsService;
    
    public VisibilityApp() {
        PlatformImpl.startup(() -> {
        });
    }
    
    public void config(List<HierarchicalConfiguration> screenConfigList, List<Module> allModulesList) {
        this.allModulesList = allModulesList;
        for (HierarchicalConfiguration screenConfig : screenConfigList) {
            Screen screen = createScreen(screenConfig);
            screenHashMap.put(screen.getName(), screen);
        }
    }
    
    public void runVisibilityApp(String[] args, Map<ConnectivityType, IConnectionService> connectionServicesMap) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        Screen mainScreen = screenHashMap.get(MAIN);
        BorderPane borderPane = new BorderPane();
        VBox vbox = new VBox();
        mainScreen.getElementList().forEach(e -> {
            vbox.getChildren().add(e.getNode());
            if (e.getNode() instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) e.getNode();
                toggleButton.setSelected(false);
                toggleButton.setOnAction(a -> e.onClick());
            }
        });
        borderPane.setTop(vbox);
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Screen createScreen(HierarchicalConfiguration screenConfig) {
        String name = screenConfig.getString("name") == null ? "" : screenConfig.getString("name");
        String title = screenConfig.getString("title");
        String icon = screenConfig.getString("icon") == null ? "" : screenConfig.getString("icon");
        String background = screenConfig.getString("background") == null ? "" : screenConfig.getString("background");
        List<IElement> elementList = new ArrayList<>(0);
        List<HierarchicalConfiguration> elementConfigList = screenConfig.configurationsAt(CONFIG_ELEMENT);
        for (HierarchicalConfiguration elementConfig : elementConfigList) {
            elementList.add(createElement(elementConfig));
        }
        if (screenHashMap.containsKey(name)) {
            Screen screen = screenHashMap.get(name);
            if (screen.getElementList().isEmpty()) {
                screen.setTitle(title);
                screen.setIcon(icon);
                screen.setBackground(background);
                screen.setElementList(elementList);
            }
            return screen;
        }
        return new Screen(name, title, icon, background, elementList);
    }
    
    private IElement createElement(HierarchicalConfiguration elementConfig) {
        IElement element = null;
        int x = elementConfig.getInt("[@X]");
        int y = elementConfig.getInt("[@Y]");
        if (elementConfig.getString("module") != null) {
            String name = elementConfig.getString("module");
            Module module = null;
            for (Module mod : allModulesList) {
                if (mod.getName().equals(name)) {
                    module = mod;
                    break;
                }
            }
            if (module == null) {
                LOGGER.error("Invalid switch module provided: " + name + "!!!!");
            } else {
                element = new SwitchElement(module, module.getDisplayName(), module.getIcon(), x, y);
            }
        } else if (elementConfig.getString("screen") != null) {
            String name = elementConfig.getString("screen");
            Screen screen = screenHashMap.get(name);
            //if child screen is added to the parent before it is even created create a Screen shell object and put it on the map
            if (screen == null) {
                screen = new Screen(name, "", "", "", new ArrayList<>(0));
                screenHashMap.put(name, screen);
            }
            element = new ScreenElelement(screen, x, y);
        }
        if (element != null) {
            element.setActorsService(actorsService);
        }
        return element;
    }
    
    public void setActorsService(ActorsService actorsService) {
        this.actorsService = actorsService;
    }
}
