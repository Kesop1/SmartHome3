package com.piotrak.servers.net;

import com.piotrak.connectivity.VisibilityCommand;
import com.piotrak.modularity.modules.ServerModule;
import com.piotrak.modularity.modules.ServerModuleUtils;
import com.piotrak.rules.Rules;
import com.piotrak.servers.IServerHandler;
import com.piotrak.servers.ServerMessage;
import com.piotrak.types.ServerType;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.piotrak.servers.ServerCnsts.*;

public class NetServerHandler implements IServerHandler {
    
    public static final Logger LOGGER = Logger.getLogger(NetServerHandler.class);
    
    private static final ServerType serverType = ServerType.NET;
    
    private NetServer server;
    
    private Rules rules;
    
    private List<NetServerListener> serverListenerList = new ArrayList<>(0);
    
    private int serverPort;
    
    public NetServerHandler(int serverPort) {
        this.serverPort = serverPort;
    }
    
    @Override
    public void createServer(Rules rules) {
        this.rules = rules;
        server = new NetServer(serverPort);
    }
    
    @Override
    public void runServer() {
        connect();
        if (server.getServerSocket() != null && !server.getServerSocket().isClosed()) {
            try {
                do {
                    Socket socket = server.getServerSocket().accept();
                    NetClient client = new NetClient(socket, "Net" + SERVER_CLIENT + serverListenerList.size());
                    server.getClients().add(client);
                    NetServerListener listener = new NetServerListener(client);
                    serverListenerList.add(listener);
                    Thread thread = new Thread(listener);
                    thread.start();
                } while (true);
            } catch (IOException e) {
                LOGGER.warn("Unable to connect new client to the server", e);
            }
        }
    }
    
    private void connect() {
        try {
            ServerSocket serverSocket = new ServerSocket(server.getPort());
            server.setServerSocket(serverSocket);
            LOGGER.info("Application Server started: " + serverSocket.getLocalSocketAddress());
        } catch (IOException e) {
            LOGGER.error("Unable to create application server on port " + server.getPort());
        }
    }
    
    @Override
    public void sendMessage(ServerMessage message) {
        if (ServerType.NET != message.getServerType()) {
            LOGGER.warn("Unable to send the message " + message.getMessageContent() + " from a " + this.getClass().getName());
            return;
        }
        if (!serverListenerList.isEmpty()) {
            serverListenerList.get(0).sendMessage((NetServerMessage) message);
        }
    }
    
    @Override
    public ServerType getServerType() {
        return serverType;
    }
    
    private class NetServerListener implements Runnable {
        
        private NetClient client;
        
        private PrintWriter out = null;
        
        private BufferedReader in = null;
        
        private NetServerListener(NetClient client) {
            this.client = client;
            try {
                in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
                out = new PrintWriter(client.getSocket().getOutputStream(), true);
            } catch (IOException e) {
                LOGGER.warn("Error occurred when getting the stream readers for the server");
            }
        }
        
        private void sendInitialConfig() {
            StringBuilder sb = new StringBuilder(0);
            sb.append(SERVER_CONFIG);
            sb.append(ServerModuleUtils.getModuleListAsString(rules.getModuleList()));
            sb.append(" ").append(SERVER_CONFIG_END);
            sendMessage(new NetServerMessage(sb.toString()));
        }
        
        public void redistributeMessage(NetServerMessage message) {
            LOGGER.info("Sending out a " + message.getClass().getName() + ": " + message.getMessageContent() + " to " +
                    (message.getClientList().isEmpty() ? " all clients" : message.getClientList().toString()));
            for (NetServerListener listener : serverListenerList) {
                if (message.getClientList().isEmpty() || message.getClientList().contains(listener.getClient().getName())) {
                    LOGGER.debug("Sending message to: " + listener.getClient().getName());
                    listener.sendMessage(message);
                }
            }
        }
        
        public void sendMessage(NetServerMessage message) {
            LOGGER.info("Sending out a " + message.getClass().getName() + ": " + message.getMessageContent() + " to " +
                    client.getName());
            out.println(message.getMessageContent());
            out.flush();
        }
        
        @Override
        public void run() {
            String clientMessage;
            try {
                do {
                    clientMessage = getMessage();
                    if (!StringUtils.isEmpty(clientMessage)) {
                        LOGGER.info("Message received from client " + client.getName() + ": " + clientMessage);
                        if (clientMessage.contains(CLIENT_CONFIG_READY)) {
                            sendInitialConfig();
                        } else if (clientMessage.contains(CLIENT_CONFIG)) {
                            getClientConfig(clientMessage);
                        } else if (clientMessage.contains(VISIBILITY_CMD)) {
                            visibilityMessageReceived(clientMessage);
                        }
                    }
                }
                while (true);
            } catch (SocketException e) {
                closeListener();
            }
        }
        
        private String getMessage() throws SocketException {
            try {
                return in.readLine();
            } catch (SocketException e) {
                throw e;
            } catch (IOException e) {
                LOGGER.error("Error while getting message from the client: " + client.getName(), e);
            }
            return "";
        }
        
        private void getClientConfig(String message) {
            String clientName = message.substring(message.indexOf("Name=") + 5);
            if (StringUtils.isNotEmpty(clientName)) {
                client.setName(clientName);
            }
        }
        
        private void visibilityMessageReceived(String message) {
            String moduleName = message.substring(message.indexOf("ServerModule=") + 7, message.indexOf(", commandText="));
            String commandText = message.substring(message.indexOf(", commandText=") + 14, message.indexOf(", commandValue="));
            String commandValueText = message.substring(message.indexOf(", commandValue=") + 16);
            int commandValue = NumberUtils.toInt(commandValueText, 0);
            ServerModule module = null;
            for (ServerModule m : rules.getModuleList()) {
                if (moduleName.equals(m.getName())) {
                    module = m;
                    break;
                }
            }
            if (module != null) {
                VisibilityCommand command = new VisibilityCommand(commandText, commandValue, module);
                rules.act(command);
            } else {
                LOGGER.warn("Incorrect command received from " + client.getName() + ", no such module: " + moduleName);
            }
        }
        
        private void closeListener() {
            try {
                client.getSocket().close();
                in.close();
                out.close();
            } catch (IOException e) {
                LOGGER.error("Error while closing the connection for the client: " + client.getName(), e);
            }
        }
        
        public NetClient getClient() {
            return client;
        }
    }
}
