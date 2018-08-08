package com.piotrak.contract.modularity.rules;

import com.piotrak.Constants;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTCommand;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;
import org.apache.log4j.Logger;

public interface IRules {
    
    Logger LOGGER = Logger.getLogger(MQTTConnection.class);
    
    void useRules(ICommand command, Module module, IConnection connection);
    
    /**
     * defaul;t implementation, send out the message received to the module's connection
     *
     * @param command
     * @param module
     * @param connection
     */
    default void onButtonClick(VisibilityCommand command, Module module, IConnection connection) {
        if (connection.isConnected()) {
            if (connection instanceof MQTTConnection) {
                connection.sendCommand(new MQTTCommand(module.getCommunication().getCommunicationMap().get(Constants.MQTT_TOPIC_SUBSCRIBE), command.getCommandText()));
            }
        } else {
            LOGGER.error("Connection was not established, unable to perform action based on Button click");
            return;
        }
        LOGGER.info(command.getCommandText() + " command sent to module: " + module.getName());
    }
}
