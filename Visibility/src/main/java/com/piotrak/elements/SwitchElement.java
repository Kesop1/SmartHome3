package com.piotrak.elements;

import com.piotrak.IElement;
import com.piotrak.contract.connectivity.ActorsService;
import com.piotrak.contract.connectivity.ICommand;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

import java.util.List;

public class SwitchElement implements IElement {
    
    private Module module;
    
    private String icon;
    
    private Node node;
    
    private ActorsService actorsService;
    
    public SwitchElement(Module module, String title, String icon, int x, int y) {
        this.module = module;
        this.icon = icon;
        this.node = new ToggleButton(title);
        node.setLayoutX(x);
        node.setLayoutY(y);
    }
    
    @Override
    public String getTitle() {
        return node.getId();
    }
    
    @Override
    public String getIcon() {
        return icon;
    }
    
    @Override
    public int getX() {
        return (int) node.getLayoutX();
    }
    
    @Override
    public int getY() {
        return (int) node.getLayoutY();
    }
    
    @Override
    public Node getNode() {
        return node;
    }
    
    @Override
    public void setActorsService(ActorsService actorsService) {
        this.actorsService = actorsService;
    }
    
    public Module getModule() {
        return module;
    }
    
    @Override
    public void onClick() {
        String command = ((ToggleButton) node).isSelected() ? "ON" : "OFF";
        List<ICommand> newCommands = module.getRules().onButtonClick(new VisibilityCommand(command, 0), module);
        if (!newCommands.isEmpty()) {
            newCommands.forEach(c -> actorsService.commandReceived(c));
        }
    }
}
