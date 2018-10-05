package com.piotrak.main;

import com.piotrak.ServicesApp;
import com.piotrak.connectivity.ConnectionServiceUtils;
import com.piotrak.connectivity.IConnectionService;
import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.modularity.modules.ServerModuleUtils;
import com.piotrak.rules.Rules;
import com.piotrak.servers.IServerHandler;
import com.piotrak.types.ConnectivityType;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static com.piotrak.servers.ServerCnsts.SERVER;

public class SmartHomeServer {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeServer.class);
    
    private List<ServerModule> moduleList = new ArrayList<>(1);
    
    private Map<ConnectivityType, IConnectionService> connectionServicesList = new EnumMap<>(ConnectivityType.class);
    
    private Rules rules;
    
    private IServerHandler serverHandler;
    
    public SmartHomeServer(XMLConfiguration configFile) {
        rules = new Rules(connectionServicesList, moduleList);
        moduleList.addAll(ServerModuleUtils.loadModules(configFile));
        connectionServicesList.putAll(ConnectionServiceUtils.loadConnectionServices(configFile, rules, moduleList));
        serverHandler = IServerHandler.createServerHandler(configFile.configurationAt(SERVER));
    }
    
    public void runServer() {
        ServicesApp servicesApp = new ServicesApp(connectionServicesList);
        servicesApp.startConnectionServices();
        
        serverHandler.createServer(rules);
        serverHandler.runServer();
    }
    
    public List<ServerModule> getModuleList() {
        return new ArrayList<>(moduleList);
    }
    
    public Map<ConnectivityType, IConnectionService> getConnectionServicesList() {
        return new EnumMap<>(connectionServicesList);
    }
    
    public IServerHandler getServerHandler() {
        return serverHandler;
    }
    
    public Rules getRules() {
        return rules;
    }
}
