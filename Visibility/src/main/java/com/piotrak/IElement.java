package com.piotrak;

import com.piotrak.contract.connectivity.ActorsService;
import javafx.scene.Node;

public interface IElement {
    
    String getTitle();
    
    String getIcon();
    
    int getX();
    
    int getY();
    
    void onClick();
    
    Node getNode();
    
    void setActorsService(ActorsService actorsService);
    
}
