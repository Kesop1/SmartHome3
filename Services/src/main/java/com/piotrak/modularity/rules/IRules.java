package com.piotrak.modularity.rules;

import com.piotrak.connectivity.ICommand;
import com.piotrak.connectivity.IConnection;
import com.piotrak.modularity.modules.Module;

public interface IRules {
    
    void useRules(ICommand command, Module module, IConnection connection);
}
