package com.piotrak.elements;

import com.piotrak.IElement;
import com.piotrak.Module;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;

import java.io.PrintWriter;

public class SwitchElement implements IElement {
    
    private Module module;
    
    private String icon;
    
    private Node node;
    
    private PrintWriter printWriter;
    
    public SwitchElement(Module module, int x, int y) {
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
    
    @Override
    public void setPrintWriter(PrintWriter out) {
        printWriter = out;
    }
    
    @Override
    public void onClick() {
        String command = ((ToggleButton) node).isSelected() ? "ON" : "OFF";
        //send command to the server
        printWriter.println("Module=" + module.getName() + ", commandText=" + command + ", commandValue=" + 0);
        printWriter.flush();
    }
}
