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
    
    private Thread thread;
    
    public SmartHomeClient(XMLConfiguration configFile) {
        config(configFile);
    }
    
    public void run() {
        thread = clientHandler.runClient();
        do {
            //wait for config from the server
        } while (!clientHandler.isConfigReceived());
        moduleList = clientHandler.getClientModuleList();
        VisibilityApp.runVisibilityApp(null);
        do {
            //work while connected to the server
        } while ((clientHandler.isConnected()));
        thread = null; //TODO: nie chce tu wejsc
        reconnect();
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
    
    private void reconnect() {
        do {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                LOGGER.info("Error occurred when sleeping", e);
            }
            thread = clientHandler.runClient();
        } while (thread == null);
    }
}
