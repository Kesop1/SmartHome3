package com.piotrak.servers.net;

import com.piotrak.modularity.ClientModule;
import com.piotrak.servers.IClientHandler;
import com.piotrak.servers.ServerMessage;
import com.piotrak.types.ServerType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import static com.piotrak.servers.ServerCnsts.*;

public class NetClientHandler implements IClientHandler {
    
    public static final Logger LOGGER = Logger.getLogger(NetClientHandler.class);
    
    private static final ServerType serverType = ServerType.NET;
    
    private String host;
    
    private int port;
    
    private NetClient client;
    
    private boolean configReceived = false;
    
    private NetClientListener listener;
    
    private List<ClientModule> serverModuleList = new ArrayList<>(0);
    
    public NetClientHandler(String host, int port, String name) {
        this.host = host;
        this.port = port;
        client = new NetClient(null, name);
    }
    
    @Override
    public boolean runClient() {
        do {
            connectToServer();
        }
        while (client.getSocket() == null);
        listener = new NetClientListener(client.getSocket());
        Thread thread = new Thread(listener);
        thread.start();
        return true;
    }
    
    @Override
    public ServerType getServerType() {
        return serverType;
    }
    
    @Override
    public boolean isConfigReceived() {
        return configReceived;
    }
    
    @Override
    public void sendMessage(ServerMessage message) {
        IClientHandler.super.sendMessage(message);
        listener.sendMessage((NetServerMessage) message);
    }
    
    private boolean connectToServer() {
        try {
            Socket socket = new Socket(host, port);
            client.setSocket(socket);
            LOGGER.info("Successfully connected to the server");
        } catch (IOException e) {
            LOGGER.error("Unable to connect to the server: " + host + ":" + port);
            return false;
        }
        return true;
    }
    
    public List<ClientModule> getClientModuleList() {
        return serverModuleList;
    }
    
    @Override
    public void requestInitialConfig() {
        sendMessage(new NetServerMessage(CLIENT_CONFIG_READY));
    }
    
    @Override
    public void sendInitialConfig() {
        sendMessage(new NetServerMessage(CLIENT_CONFIG + "Name=" + client.getName() + " " + CLIENT_CONFIG_END));
    }
    
    private class NetClientListener implements Runnable {
        
        private Socket socket;
        
        private BufferedReader in;
        
        private PrintWriter out;
        
        public NetClientListener(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(client.getSocket().getInputStream()));
                out = new PrintWriter(client.getSocket().getOutputStream(), true);
            } catch (IOException e) {
                LOGGER.warn("Error occurred when getting the stream readers for the server");
            }
        }
        
        @Override
        public void run() {
            try {
                sendInitialConfig();
                requestInitialConfig();
                do {
                    NetServerMessage message = getMessage();
                    if (message != null) {
                        String content = message.getMessageContent();
                        if (content.startsWith(SERVER_CONFIG)) {
                            configReceived = true;
                        } else {
                            //different type of message from the server
                        }
                    }
                }
                while (true);
            } catch (IOException e) {
                LOGGER.warn("Error occurred while running the socket", e);
            } finally {
                try {
                    if (client.getSocket() != null) {
                        client.getSocket().close();
                        in.close();
                        out.close();
                    }
                } catch (IOException e) {
                    LOGGER.warn("Error occurred while closing the socket", e);
                }
            }
        }
        
        public void sendMessage(NetServerMessage message) {
            out.println(message.getMessageContent());
            out.flush();
        }
    
        private NetServerMessage getMessage() throws SocketException {
            NetServerMessage netServerMessage = null;
            String message = "";
            try {
                message = in.readLine();
                LOGGER.info("Message received from the server: " + message);
            } catch (SocketException e) {
                throw e;
            } catch (IOException e) {
                LOGGER.error("Error while getting message from the server", e);
            }
            if (!StringUtils.isEmpty(message)) {
                netServerMessage = new NetServerMessage(message);
            }
            return netServerMessage;
        }
        
    }
    
}
