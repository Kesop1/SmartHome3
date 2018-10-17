package com.piotrak;

import com.piotrak.connectivity.VisibilityCommand;
import com.piotrak.elements.SwitchElement;
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

public class VisibilityApp extends Application {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(VisibilityApp.class); //TODO: lokalizacja pliku na Win i linux
    
    public static final String MAIN_SCREEN = "Main";
    
    private static IClientHandler clientHandler;
    
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
        Screen mainScreen = VisibilityAppUtils.getScreenMap().get(MAIN_SCREEN);
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
                    clientHandler.sendMessage(new NetServerMessage(
                            new VisibilityCommand(toggleButton.isSelected() ? "ON" : "OFF", 0, module).toString()));
                });
            }
        });
        borderPane.setTop(vbox);
        Scene scene = new Scene(borderPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
