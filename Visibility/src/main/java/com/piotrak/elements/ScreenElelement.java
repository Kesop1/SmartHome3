package com.piotrak.elements;

import com.piotrak.IElement;
import com.piotrak.Screen;
import com.piotrak.contract.connectivity.ActorsService;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class ScreenElelement implements IElement {
    
    private Screen screen;
    
    private int x;
    
    private int y;
    
    private Node node;
    
    public ScreenElelement(Screen screen, int x, int y) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        node = new Button(screen.getTitle());
    }
    
    @Override
    public String getTitle() {
        return screen.getTitle();
    }
    
    @Override
    public String getIcon() {
        return screen.getIcon();
    }
    
    @Override
    public int getX() {
        return x;
    }
    
    @Override
    public int getY() {
        return y;
    }
    
    public Screen getScreen() {
        return screen;
    }
    
    @Override
    public void setActorsService(ActorsService actorsService) {
        //not needed
    }
    
    @Override
    public void onClick() {
    
    }
    
    @Override
    public Node getNode() {
        return node;
    }
}
