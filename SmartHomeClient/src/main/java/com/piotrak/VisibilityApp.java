package com.piotrak;

import com.piotrak.elements.SwitchElement;
import com.piotrak.modularity.ClientModule;
import com.piotrak.modularity.Module;
import com.piotrak.servers.IClientHandler;
import com.piotrak.servers.net.NetServerMessage;
import javafx.application.Application;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.configuration.HierarchicalConfiguration;

import java.util.List;
import java.util.Map;

public class VisibilityApp extends Application {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VisibilityApp.class); //TODO: lokalizacja pliku na Win i linux
    
    public static final String MAIN_SCREEN = "Main";
    
    private static List<ClientModule> moduleList;
    
    private static IClientHandler clientHandler;
    
    private static Map<String, Screen> screenHashMap;
    
    public VisibilityApp() {
        //no-argument constructor required for the application to launch
    }
    
    public static void runVisibilityApp(String[] args) {
        launch(args);
    }
    
    public static void config(HierarchicalConfiguration screensConfig, IClientHandler handler) {
        JFXPanel fxPanel = new JFXPanel(); //workaround for JavaFX issue
        clientHandler = handler;
        VisibilityAppUtils.createScreens(screensConfig);
        VisibilityAppUtils.createElements(screensConfig);
    }
    
    @Override
    public void start(Stage primaryStage) {
        Screen mainScreen = screenHashMap.get(MAIN_SCREEN);
        BorderPane borderPane = new BorderPane();
        VBox vbox = new VBox();
        mainScreen.getElementList().forEach(e -> {
            vbox.getChildren().add(e.getNode());
            if (e.getNode() instanceof ToggleButton) {
                ToggleButton toggleButton = (ToggleButton) e.getNode();
                toggleButton.setSelected(false);
                toggleButton.setOnAction(a -> {
                    Module module = null;
                    if (e instanceof SwitchElement) {
                        module = ((SwitchElement) e).getModule();
                    }
                    clientHandler.sendMessage(new NetServerMessage(toggleButton.isSelected() ? "ON" : "OFF", module));
                });
            }
        });
        borderPane.setTop(vbox);
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


//    private void configScreens(List<HierarchicalConfiguration> screenConfigList) {
//        for (HierarchicalConfiguration screenConfig : screenConfigList) {
//            Screen screen = createScreen(screenConfig);
//            screen.getElementList().forEach(e -> {
//                e.setPrintWriter(out);
//            });
//            screenHashMap.put(screen.getName(), screen);
//        }
//    }

//    private Screen createScreen(HierarchicalConfiguration screenConfig) {
//        String name = screenConfig.getString("name") == null ? "" : screenConfig.getString("name");
//        String title = screenConfig.getString("title");
//        String icon = screenConfig.getString("icon") == null ? "" : screenConfig.getString("icon");
//        String background = screenConfig.getString("background") == null ? "" : screenConfig.getString("background");
//        List<Element> elementList = new ArrayList<>(0);
//        List<HierarchicalConfiguration> elementConfigList = screenConfig.configurationsAt(CONFIG_ELEMENT);
//        for (HierarchicalConfiguration elementConfig : elementConfigList) {
//            Element element = createElement(elementConfig);
//            if (element != null) {
//                elementList.add(element);
//            }
//        }
//        if (screenHashMap.containsKey(name)) {
//            Screen screen = screenHashMap.get(name);
//            if (screen.getElementList().isEmpty()) {
//                screen.setTitle(title);
//                screen.setIcon(icon);
//                screen.setBackground(background);
//                screen.setElementList(elementList);
//            }
//            return screen;
//        }
//        return new Screen(name, title, icon, background, elementList);
//    }

//    private Element createElement(HierarchicalConfiguration elementConfig) {
//        Element element = null;
//        int x = elementConfig.getInt("[@X]");
//        int y = elementConfig.getInt("[@Y]");
//        if (elementConfig.getString("module") != null) {
//            String name = elementConfig.getString("module");
//            Module module = null;
//
//            //check if the module was defined in the server app config
//            for (Module m : moduleList) {
//                if (name.equals(m.getName())) {
//                    module = m;
//                    break;
//                }
//            }
//            if (module == null) {
//                LOGGER.warn("ServerModule " + name + " was not defined in the server app config file");
//                return null;
//            }
//
//            if (module.getModuleType() == ModuleType.SWITCH) {
//                element = new SwitchElement(module, x, y);
//            } else {
//                LOGGER.error("Unknown ServerModule type: " + module.getModuleType().name() + ". Unable to create the screen element");
//            }
//        } else if (elementConfig.getString("screen") != null) {
//            String name = elementConfig.getString("screen");
//            Screen screen = screenHashMap.get(name);
//            //if child screen is added to the parent before it is even created create a Screen shell object and put it on the map
//            if (screen == null) {
//                screen = new Screen(name, "", "", "", new ArrayList<>(0));
//                screenHashMap.put(name, screen);
//            }
//            element = new ScreenElement(screen, x, y);
//        }
//        return element;
//    }
    
}
