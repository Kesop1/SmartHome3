package com.piotrak.connectivity;

import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.modularity.modules.ServerModuleUtils;
import com.piotrak.types.ConnectivityType;

import java.util.List;

public interface IConnectionService {
    
    IModuleConnection getConnection();
    
    List<ServerModule> getModulesList();
    
    void actOnCommand(Command command);
    
    void startService();
    
    void config(List<ServerModule> modules, IModuleConnection connection);
    
    default List<ServerModule> getModulesPerConnectivityType(List<ServerModule> moduleList) {
        return ServerModuleUtils.getModulesByCommunicationType(getConnectivityType(), moduleList);
    }
    
    ConnectivityType getConnectivityType();
    
}
