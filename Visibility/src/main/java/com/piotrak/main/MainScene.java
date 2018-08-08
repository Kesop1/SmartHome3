package com.piotrak.main;

import com.piotrak.IScene;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.modularity.modules.SwitchModule;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainScene implements IScene {
    
    public static final String APP_MAIN_TITLE = "SmartHome";
    
    private List<Module> moduleList;
    
    private Map<String, IConnectionService> connectionServiceMap;
    
    private List<ButtonBase> buttonsList = new ArrayList<>(0);
    
    public MainScene(List<Module> moduleList, Map<String, IConnectionService> connectionServiceMap) {
        this.moduleList = moduleList;
        this.connectionServiceMap = connectionServiceMap;
    }
    
    public void start(Stage primaryStage) {
        primaryStage.setTitle(APP_MAIN_TITLE);
        StackPane layout = new StackPane();
        setButtons(layout);
        
        Scene scene = new Scene(layout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }
    
    @Override
    public void setButtons(Pane pane) {
        moduleList.forEach(module -> {
            ButtonBase button;
            if (module instanceof SwitchModule) {
                button = new ToggleButton();
//                button.setOnAction(e -> module.getRules().onButtonClick(null);
//                        new VisibilityCommand(((ToggleButton) button).selectedProperty().getValue() ? "ON" : "OFF", 0), module, );
            } else {
                button = new Button();
            }
            button.setText(module.getDisplayName());
            pane.getChildren().add(button);
            buttonsList.add(button);
        });
    }
    
    @Override
    public void setTitle(Stage stage) {
        stage.setTitle(APP_MAIN_TITLE);
    }
    
    @Override
    public List<Module> getModulesList() {
        return moduleList;
    }
    
    @Override
    public Map<String, IConnectionService> getConnectionServicesMap() {
        return connectionServiceMap;
    }
    
}
