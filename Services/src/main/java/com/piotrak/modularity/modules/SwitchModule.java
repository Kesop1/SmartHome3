package com.piotrak.modularity.modules;

import com.piotrak.modularity.Module;
import org.apache.commons.configuration.HierarchicalConfiguration;

public class SwitchModule extends Module {
    
    private String topicSubscribe;
    
    private String topicPublish;
    
    @Override
    public void config(HierarchicalConfiguration config) {
        super.config(config);
        topicSubscribe = config.getString("topic-subscribe") == null ? "" : config.getString("topic-subscribe");
        topicPublish = config.getString("topic-publish") == null ? "" : config.getString("topic-publish");
    }
}
