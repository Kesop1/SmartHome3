package com.piotrak;

import org.apache.commons.lang.StringUtils;

import java.util.List;

public class Screen {
    
    private String name;
    
    private String title;
    
    private String icon;
    
    private String background;
    
    private List<IElement> elementList;
    
    public Screen(String name, String title, String icon, String background, List<IElement> elementList) {
        this.name = name;
        this.title = StringUtils.isEmpty(title) ? name : title;
        this.icon = icon;
        this.background = background;
        this.elementList = elementList;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getBackground() {
        return background;
    }
    
    public void setBackground(String background) {
        this.background = background;
    }
    
    public List<IElement> getElementList() {
        return elementList;
    }
    
    public void setElementList(List<IElement> elementList) {
        this.elementList = elementList;
    }
}
