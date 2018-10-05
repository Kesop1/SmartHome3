package com.piotrak.servers.net;

import com.piotrak.modularity.Module;
import com.piotrak.servers.ServerMessage;
import com.piotrak.types.ServerType;

import java.util.ArrayList;
import java.util.List;

public class NetServerMessage extends ServerMessage {
    
    private static final ServerType serverType = ServerType.NET;
    
    private String messageContent;
    
    private Module module;
    
    public NetServerMessage(String messageContent) {
        this.messageContent = messageContent;
        setClientList(new ArrayList<>(0));
    }
    
    public NetServerMessage(String messageContent, List<String> clientList) {
        this.messageContent = messageContent;
        setClientList(clientList);
    }
    
    public NetServerMessage(String messageContent, Module module) {
        this.messageContent = messageContent;
        this.module = module;
        setClientList(new ArrayList<>(0));
    }
    
    public NetServerMessage(String messageContent, Module module, List<String> clientList) {
        this.messageContent = messageContent;
        this.module = module;
        setClientList(clientList);
    }
    
    @Override
    public ServerType getServerType() {
        return serverType;
    }
    
    @Override
    public String getMessageContent() {
        return messageContent;
    }
    
    @Override
    public Module getModule() {
        return module;
    }
    
    @Override
    public String toString() {
        return "Net" + super.toString();
    }
}
