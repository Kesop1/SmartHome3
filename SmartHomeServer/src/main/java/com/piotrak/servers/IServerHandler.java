package com.piotrak.servers;

import com.piotrak.rules.Rules;
import com.piotrak.servers.net.NetServerHandler;
import com.piotrak.types.ServerType;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.log4j.Logger;

import java.util.List;

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
    
    void setUpClient(Client client);
    
    void createServer(Rules rules);
    
    void runServer();
    
    ServerType getServerType();
    
    List<Client> getConnectedClients();
    
    default void sendMessage(ServerMessage message) {
        LOGGER.info("Sending out a " + message.getClass().getSimpleName() + ": " + message.getMessageContent() + " to: " + message.getClientsString());
        for (String client : message.getClientList()) {
            for (Client connected : getConnectedClients()) {
                if (client.equals(connected.getName())) {
                    sendMessageToClient(message, connected);
                    break;
                }
            }
        }
    }
    
    void sendMessageToClient(ServerMessage message, Client client);
    
}
