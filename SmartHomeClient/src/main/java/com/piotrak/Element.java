package com.piotrak;

import javafx.scene.Node;

public abstract class Element {
    
    private String title;
    
    private String icon;
    
    private int x;
    
    private int y;
    
    public Element(String title, String icon, int x, int y) {
        this.title = title;
        this.icon = icon;
        this.x = x;
        this.y = y;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    @Override
    public String toString() {
        return "Element: " + getTitle();
    }
    
    public abstract Node getNode();
    
}
