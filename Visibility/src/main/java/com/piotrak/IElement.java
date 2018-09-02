package com.piotrak;

import javafx.scene.Node;

import java.io.PrintWriter;

public interface IElement {
    
    String getTitle();
    
    String getIcon();
    
    int getX();
    
    int getY();
    
    void onClick();
    
    Node getNode();
    
    void setPrintWriter(PrintWriter out);
    
}
