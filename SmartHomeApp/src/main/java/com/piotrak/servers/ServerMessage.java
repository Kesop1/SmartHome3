package com.piotrak.servers;


import com.piotrak.modularity.Module;
import com.piotrak.types.ServerType;
import org.apache.commons.lang.text.StrBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class ServerMessage {
    
    List<String> clientList = new ArrayList<>(0);
    
    public abstract ServerType getServerType();
    
    public abstract String getMessageContent();
    
    public abstract Module getModule();
    
    public List<String> getClientList() {
        return clientList;
    }
    
    public void setClientList(List<String> clientList) {
        this.clientList = clientList;
    }
    
    @Override
    public String toString() {
        StrBuilder sb = new StrBuilder(0);
        sb.append("ServerMessage: ").append(getMessageContent());
        if (getModule() != null) {
            sb.append(", Module: ").append(getModule().getName());
        }
        if (!clientList.isEmpty()) {
            sb.append(", ClientList: ");
            clientList.forEach((c -> sb.append(c).append(", ")));
        }
        return sb.toString();
    }
}
