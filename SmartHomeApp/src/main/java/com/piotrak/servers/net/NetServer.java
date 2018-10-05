package com.piotrak.servers.net;

import com.piotrak.servers.Server;
import com.piotrak.types.ServerType;
import org.apache.log4j.Logger;

import java.net.ServerSocket;

public class NetServer extends Server implements NetConnection {
    
    public static final Logger LOGGER = Logger.getLogger(NetServer.class);
    
    private ServerSocket serverSocket;
    
    private int port;
    
    public NetServer(int serverPort) {
        this.port = serverPort;
    }
    
    public ServerSocket getServerSocket() {
        return serverSocket;
    }
    
    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }
    
    @Override
    public int getPort() {
        return port;
    }
    
    @Override
    public String getHost() {
        if (serverSocket == null) {
            return "";
        }
        return serverSocket.getInetAddress().getHostAddress();
    }
    
    @Override
    public ServerType getServerType() {
        return SERVER_TYPE;
    }
}
