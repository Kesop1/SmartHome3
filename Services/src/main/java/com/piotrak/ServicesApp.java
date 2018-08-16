package com.piotrak;

import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.log4j.Logger;

import java.util.Map;

public class ServicesApp {
    
    public static final Logger LOGGER = Logger.getLogger(ServicesApp.class);
    
    private Map<ConnectivityType, IConnectionService> connectionServiceList;
    
    public ServicesApp(Map<ConnectivityType, IConnectionService> connectionServiceList) {
        this.connectionServiceList = connectionServiceList;
    }
    
    public void connect() {
        for (IConnectionService connectionService : connectionServiceList.values()) {
            connectionService.startService();
        }
    }
}
