package com.piotrak.elements;

import com.piotrak.IElement;
import com.piotrak.modularity.ClientModule;
import com.piotrak.modularity.Module;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public class SwitchElement implements IElement {
    
    private ClientModule module;
    
    private String icon;
    
    private Node node;
    
    public SwitchElement(ClientModule module, int x, int y) {
        this.module = module;
        this.icon = module.getIcon();
        this.node = new ToggleButton(module.getDisplayName());
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
    
    public Module getModule() {
        return module;
    }
    
}
