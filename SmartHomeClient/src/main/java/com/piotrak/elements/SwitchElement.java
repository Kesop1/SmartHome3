package com.piotrak.elements;

import com.piotrak.Element;
import com.piotrak.modularity.ClientModule;
import com.piotrak.modularity.Module;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

public class SwitchElement extends Element {
    
    private ClientModule module;
    
    private Node node;
    
    public SwitchElement(ClientModule module, int x, int y) {
        super(module.getDisplayName(), module.getIcon(), x, y);
        this.module = module;
        this.node = new ToggleButton(module.getDisplayName());
        node.setLayoutX(x);
        node.setLayoutY(y);
    }
    
    @Override
    public Node getNode() {
        return node;
    }
    
    public Module getModule() {
        return module;
    }
    
    @Override
    public String toString() {
        return "Switch" + super.toString();
    }
}
