package com.piotrak.contract.connectivity;

import com.piotrak.contract.modularity.actors.IActor;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.modularity.mqtt.actors.MQTTActor;
import com.piotrak.impl.types.ConnectivityType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActorsService {
    
    private List<IActor> actorsList;
    
    private Map<ConnectivityType, IConnectionService> connectionServicesList;
    
    public void config(Map<ConnectivityType, IConnectionService> connectionServicesList) {
        this.connectionServicesList = connectionServicesList;
        this.actorsList = getActors();
    }
    
    public void commandReceived(ICommand command) {
        List<ICommand> newCommands = new ArrayList<>(0);
        for (IActor actor : actorsList) {
            if (actor.getConnectivityType().equals(command.getConnectivityType())) {
                newCommands.addAll(actor.actOnCommand(command));
                break;
            }
        }
        newCommands.forEach(newCommand ->
                sendCommands(newCommand, connectionServicesList.get(newCommand.getConnectivityType()).getConnection()));
    }
    
    private List<IActor> getActors() {
        List<IActor> actorList = new ArrayList<>(1);
        actorList.add(getMQTTActor());
        return actorList;
    }
    
    private IActor getMQTTActor() {
        MQTTActor mqttActor = new MQTTActor();
        MQTTConnectionService mqttConnectionService = (MQTTConnectionService) connectionServicesList.get(ConnectivityType.MQTT);
        mqttActor.setTopicsMap(mqttConnectionService.getTopicsMap());
        return mqttActor;
    }
    
    private void sendCommands(ICommand command, IConnection connection) {
        connection.sendCommand(command);
    }
    
    
    public List<IActor> getActorsList() {
        return new ArrayList<>(actorsList);
    }
    
}
