package com.piotrak;

import com.piotrak.connectivity.IConnectionService;
import com.piotrak.types.ConnectivityType;
import org.apache.log4j.Logger;

import java.util.Map;

public class ServicesApp {
    
    public static final Logger LOGGER = Logger.getLogger(ServicesApp.class);
    
    private Map<ConnectivityType, IConnectionService> connectionServiceList;
    
    public ServicesApp(Map<ConnectivityType, IConnectionService> connectionServiceList) {
        this.connectionServiceList = connectionServiceList;
    }
    
    public void startConnectionServices() {
        for (IConnectionService connectionService : connectionServiceList.values()) {
            connectionService.startService();
        }
    }
}
