package com.piotrak.main;

import com.piotrak.VisibilityApp;
import com.piotrak.modularity.ClientModule;
import com.piotrak.servers.IClientHandler;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.List;

import static com.piotrak.servers.ServerCnsts.SERVER;

public class SmartHomeClient {
    
    public static final org.apache.log4j.Logger LOGGER = org.apache.log4j.Logger.getLogger(SmartHomeClient.class); //TODO: lokalizacja pliku na Win i linux
    
    private List<ClientModule> moduleList;
    
    private IClientHandler clientHandler;
    
    public SmartHomeClient(XMLConfiguration configFile) {
        clientHandler = IClientHandler.createServerHandler(configFile.configurationAt(SERVER));
        if (clientHandler == null) {
            System.exit(0);
        }
        clientHandler.runClient();
        do {
            //wait for config from the server
        } while (!clientHandler.isConfigReceived());
        moduleList = clientHandler.getClientModuleList();
    }
    
    public void runClient(XMLConfiguration configFile) {
//        clientHandler.runClient();
        VisibilityApp.config(configFile.configurationAt("screens"), moduleList, clientHandler);
        VisibilityApp.runVisibilityApp(null);
    }
    
    public IClientHandler getClientHandler() {
        return clientHandler;
    }
}
