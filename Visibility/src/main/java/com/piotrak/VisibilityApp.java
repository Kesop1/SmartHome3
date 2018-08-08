package com.piotrak;

import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.main.MainScene;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class VisibilityApp extends Application {
    
    private static List<Module> moduleList;
    
    private static Map<String, IConnectionService> connectionServiceMap;
    
    public static void main(String[] args, List<Module> modulesList, Map<String, IConnectionService> connectionServicesMap) {
        moduleList = modulesList;
        connectionServiceMap = connectionServicesMap;
        launch(args);
        //TODO: przyjmij konfiguracje, a w niej lokalizacje przycisków, na ekranie i na których ekranach ma się pojawiać
        //TODO: wszystkie przyciski tutaj twórz, a do poszczególnych ekranów wysyłaj listę, każdy przycisk ma listę ekranów i swoją popzycję na każdym ekranie
    }
    
    @Override
    public void start(Stage primaryStage) {
        MainScene mainScene = new MainScene(moduleList, connectionServiceMap);
        mainScene.start(primaryStage);
    }
}
