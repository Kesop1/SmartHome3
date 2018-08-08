package com.piotrak;

import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public interface IScene {
    
    List<Module> getModulesList();
    
    Map<String, IConnectionService> getConnectionServicesMap();
    
    void setButtons(Pane pane);
    
    void setTitle(Stage stage);
}
