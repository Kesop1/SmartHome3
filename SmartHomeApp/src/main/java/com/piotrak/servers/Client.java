package com.piotrak.servers;


import com.piotrak.types.ServerType;

public abstract class Client {
    private String name;
    
    public Client(String name) {
        this.name = name;
    }
    
    public abstract ServerType getServerType();
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
}
