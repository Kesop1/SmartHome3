package com.piotrak;

public class Main {
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static void main(String[] args) {
        SmartHomeApp app = new SmartHomeApp();
        String config = CONFIG_FILE;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-C") && args.length > i + 1) {
                    config = args[i + 1];
                }
            }
        }
        app.loadConfig(config);
        app.connect();
    }
}
