package com.piotrak.servers.net;

import com.piotrak.types.ServerType;

public interface NetConnection {
    
    ServerType SERVER_TYPE = ServerType.NET;
    
    String getHost();
    
    int getPort();
    
}
