package com.piotrak.main;

import com.piotrak.VisibilityApp;
import com.piotrak.modularity.ClientModule;
import com.piotrak.servers.IClientHandler;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.List;

import static com.piotrak.servers.ServerCnsts.SERVER;

public class SmartHomeClient {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SmartHomeClient.class); //TODO: lokalizacja pliku na Win i linux
    
    private List<ClientModule> moduleList;
    
    private IClientHandler clientHandler;
    
    public SmartHomeClient(XMLConfiguration configFile) {
        config(configFile);
        clientHandler.runClient();
        do {
            //wait for config from the server
        } while (!clientHandler.isConfigReceived());
        moduleList = clientHandler.getClientModuleList();
    }
    
    public void run() {
        VisibilityApp.runVisibilityApp(null);
    }
    
    private void config(XMLConfiguration configFile) {
        getClientHandler(configFile.configurationAt(SERVER));
        VisibilityApp.config(configFile.configurationAt("screens"), clientHandler);
    }
    
    private void getClientHandler(HierarchicalConfiguration serverConfig) {
        clientHandler = IClientHandler.createClientHandler(serverConfig);
        if (clientHandler == null) {
            System.exit(0);
        }
    }
    
}
