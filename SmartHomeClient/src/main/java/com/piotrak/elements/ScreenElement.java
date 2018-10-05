package com.piotrak.elements;

import com.piotrak.Element;
import com.piotrak.Screen;
import javafx.scene.Node;
import javafx.scene.control.Button;

public class ScreenElement extends Element {
    
    private Screen screen;
    
    private Node node;
    
    public ScreenElement(Screen screen, int x, int y) {
        super(screen.getTitle(), screen.getIcon(), x, y);
        this.screen = screen;
        node = new Button(screen.getTitle());
    }

    public Screen getScreen() {
        return screen;
    }
    
    @Override
    public Node getNode() {
        return node;
    }
    
    @Override
    public String toString() {
        return "Screen" + super.toString();
    }
}
