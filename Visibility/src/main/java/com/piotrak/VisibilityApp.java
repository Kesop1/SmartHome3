package com.piotrak;

import com.piotrak.elements.ScreenElelement;
import com.piotrak.elements.SwitchElement;
import com.piotrak.types.ModuleType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VisibilityApp extends Application implements Runnable {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VisibilityApp.class); //TODO: lokalizacja pliku na Win i linux
    
    public static final String MAIN_SCREEN = "Main";
    
    public static final String CONFIG_FILE = "config.xml";
    
    private static final String CONFIG_ELEMENT = "elements.element";
    
    private static final String CONFIG_SCREEN = "visibility.screens.screen";
    
    private static final String SERVER_CONFIG = "srv-cfg: ";
    
    private static Map<String, Screen> screenHashMap = new HashMap<>(1);
    private static String homeDir = System.getProperty("user.home");
    private static String fileSeparator = System.getProperty("file.separator");
    private static String appName = "SmartHomeApp";
    Socket socket;
    private List<Module> moduleList = new ArrayList<>(10);
    private PrintWriter out = null;
    private BufferedReader in = null;
    
    public static void main(String[] args) {
        VisibilityApp visibilityApp = new VisibilityApp();
        boolean connected = visibilityApp.connectToServer();
        if (connected) {
            LOGGER.info("Succesfully connected to the server");
        } else {
            LOGGER.info("Unable to connect to the server");
            System.exit(0);
        }
        Thread thread = new Thread(visibilityApp);
        thread.start();
        XMLConfiguration configFile;
        try {
            //create the screens and elements on it
            LOGGER.info("Looking for the config.xml file in the path: " + homeDir + fileSeparator + appName + fileSeparator + CONFIG_FILE);
            configFile = new XMLConfiguration(homeDir + fileSeparator + appName + fileSeparator + CONFIG_FILE);
            List<HierarchicalConfiguration> screenConfigList = configFile.configurationsAt(CONFIG_SCREEN);
            visibilityApp.config(screenConfigList);
            visibilityApp.runVisibilityApp(args);
        } catch (ConfigurationException e) {
            LOGGER.error("Unable to find the config file");
        }
    }
    
    private boolean connectToServer() {
        try {
            socket = new Socket("10.0.2.15", 1024);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private void runVisibilityApp(String[] args) {
        launch(args);
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
                toggleButton.setOnAction(a -> e.onClick());
            }
        });
        borderPane.setTop(vbox);
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override
    public void run() {
        try {
            do {
                String message = in.readLine();
                LOGGER.info("Message received from server: " + message);
                if (message != null) {
                    if (message.startsWith(SERVER_CONFIG)) {
                        Module module = getModuleFromServer(message);
                        if (module != null) {
                            moduleList.add(module);
                        }
                    }
                }
            }
            while (true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private Module getModuleFromServer(String message) {
        ModuleType type = Enum.valueOf(ModuleType.class, message.substring(message.indexOf("moduleType=") + 11, message.indexOf(", name=")));
        String name = message.substring(message.indexOf(", name=") + 7, message.indexOf(", displayName="));
        String displayName = message.substring(message.indexOf(", displayName=") + 14, message.indexOf(", icon="));
        String icon = message.substring(message.indexOf(", icon=") + 7, message.length() - 1);
        try {
            return new Module(type, name, displayName, icon);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Incorrect Module arguments received from the server: " + message);
        }
        return null;
    }
    
    private void config(List<HierarchicalConfiguration> screenConfigList) {
        for (HierarchicalConfiguration screenConfig : screenConfigList) {
            Screen screen = createScreen(screenConfig);
            screen.getElementList().forEach(e -> {
                e.setPrintWriter(out);
            });
            screenHashMap.put(screen.getName(), screen);
        }
    }
    
    private Screen createScreen(HierarchicalConfiguration screenConfig) {
        String name = screenConfig.getString("name") == null ? "" : screenConfig.getString("name");
        String title = screenConfig.getString("title");
        String icon = screenConfig.getString("icon") == null ? "" : screenConfig.getString("icon");
        String background = screenConfig.getString("background") == null ? "" : screenConfig.getString("background");
        List<IElement> elementList = new ArrayList<>(0);
        List<HierarchicalConfiguration> elementConfigList = screenConfig.configurationsAt(CONFIG_ELEMENT);
        for (HierarchicalConfiguration elementConfig : elementConfigList) {
            IElement element = createElement(elementConfig);
            if (element != null) {
                elementList.add(element);
            }
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
    
            //check if the module was defined in the server app config
            for (Module m : moduleList) {
                if (name.equals(m.getName())) {
                    module = m;
                    break;
                }
            }
            if (module == null) {
                LOGGER.warn("Module " + name + " was not defined in the server app config file");
                return null;
            }
    
            if (module.getModuleType() == ModuleType.SWITCH) {
                element = new SwitchElement(module, x, y);
            } else {
                LOGGER.error("Unknown Module type: " + module.getModuleType().name() + ". Unable to create the screen element");
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
        return element;
    }
    
}
