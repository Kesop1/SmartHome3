package com.piotrak;

public class Main {
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static void main(String[] args) {
        SmartHomeApp app = new SmartHomeApp();
        app.loadConfig(CONFIG_FILE);
        app.connect();
    }
}
