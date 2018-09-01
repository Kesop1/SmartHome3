package com.piotrak;

import javafx.scene.Node;

public interface IElement {
    
    String getTitle();
    
    String getIcon();
    
    int getX();
    
    int getY();
    
    void onClick();
    
    Node getNode();
    
}
