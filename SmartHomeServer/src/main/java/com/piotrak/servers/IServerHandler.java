package com.piotrak.servers;

import com.piotrak.rules.Rules;
import com.piotrak.servers.net.NetServerHandler;
import com.piotrak.types.ServerType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;


public interface IServerHandler {
    
    Logger LOGGER = Logger.getLogger(IServerHandler.class);
    
    static IServerHandler createServerHandler(HierarchicalConfiguration serverConfig) {
        IServerHandler serverHandler = null;
        ServerType serverType = Enum.valueOf(ServerType.class, serverConfig.getString("type").toUpperCase());
        if (serverType == ServerType.NET) {
            serverHandler = new NetServerHandler(serverConfig.getInt("port"));
        }
        if (serverHandler == null) {
            LOGGER.error("Unable to create the ServerHandler");
            return null;
        }
        LOGGER.debug(serverHandler.getServerType() + " ServerHandler created");
        return serverHandler;
    }
    
    void createServer(Rules rules);
    
    void runServer();
    
    ServerType getServerType();
    
    void sendMessage(ServerMessage message);
}
