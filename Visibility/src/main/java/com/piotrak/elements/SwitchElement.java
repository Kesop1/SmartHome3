package com.piotrak.elements;

import com.piotrak.IElement;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;
import com.piotrak.impl.modularity.rules.Rules;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public class SwitchElement implements IElement {
    
    private Module module;
    
    private String icon;
    
    private Node node;
    
    private Rules rules;
    
    public SwitchElement(Module module, String title, String icon, int x, int y, Rules rules) {
        this.module = module;
        this.icon = icon;
        this.node = new ToggleButton(title);
        node.setLayoutX(x);
        node.setLayoutY(y);
        this.rules = rules;
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
    
    public Module getModule() {
        return module;
    }
    
    @Override
    public void onClick() {
        String command = ((ToggleButton) node).isSelected() ? "ON" : "OFF";
        VisibilityCommand visibilityCommand = new VisibilityCommand(command, 0, module);
        rules.act(visibilityCommand);
    }
}
