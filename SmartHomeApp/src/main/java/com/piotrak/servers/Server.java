package com.piotrak.servers;

import com.piotrak.types.ServerType;

import java.util.ArrayList;
import java.util.List;

public abstract class Server {
    
    private List<Client> clientList = new ArrayList<>(0);
    
    public abstract ServerType getServerType();
    
    public List<Client> getClients() {
        return clientList;
    }
    
}
