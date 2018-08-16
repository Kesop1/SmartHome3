package com.piotrak.impl.modularity.rules;

import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.contract.modularity.rules.IRules;

import java.util.ArrayList;
import java.util.List;

public class RulesGlosniki implements IRules {
    
    @Override
    public List<ICommand> useRules(ICommand command, Module module) {
        return new ArrayList<>(0);
    }
}
