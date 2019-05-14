package com.piotrak.servers;

import com.piotrak.modularity.ClientModule;
import com.piotrak.servers.net.NetClientHandler;
import com.piotrak.types.ServerType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import java.util.List;


public interface IClientHandler {
    
    Logger LOGGER = Logger.getLogger(IClientHandler.class);
    
    static IClientHandler createClientHandler(HierarchicalConfiguration serverConfig) {
        IClientHandler clientHandler = null;
        ServerType serverType = Enum.valueOf(ServerType.class, serverConfig.getString("type").toUpperCase());
        if (serverType == ServerType.NET) {
            clientHandler = new NetClientHandler(serverConfig.getString("host"), serverConfig.getInt("port"),
                    serverConfig.getString("client"));
        }
        if (clientHandler == null) {
            LOGGER.error("Unable to create the ClientHandler");
            return null;
        }
        LOGGER.debug(clientHandler.getServerType() + " ServerHandler created");
        return clientHandler;
    }
    
    Thread runClient();
    
    ServerType getServerType();
    
    List<ClientModule> getClientModuleList();
    
    default void sendMessage(ServerMessage message) {
        LOGGER.info("Sending out a " + message.getClass().getSimpleName() + ": " + message.getMessageContent() + " to the server");
    }
    
    boolean isConfigReceived();
    
    void requestInitialConfig();
    
    void sendInitialConfig();
    
    boolean isConnected();
}
