package com.piotrak.servers.net;

import com.piotrak.servers.Client;
import com.piotrak.types.ServerType;

import java.net.Socket;

public class NetClient extends Client implements NetConnection {
    
    private Socket socket;
    
    public NetClient(Socket socket, String name) {
        super(name);
        this.socket = socket;
    }
    
    public Socket getSocket() {
        return socket;
    }
    
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public ServerType getServerType() {
        return SERVER_TYPE;
    }
    
    @Override
    public String getHost() {
        if (socket == null) {
            return "";
        }
        return socket.getInetAddress().getHostAddress();
    }
    
    @Override
    public int getPort() {
        return socket.getPort();
    }
}
