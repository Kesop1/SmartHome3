package com.piotrak;

import com.piotrak.contract.connectivity.IConnection;
import com.piotrak.contract.connectivity.IConnectionService;
import com.piotrak.contract.modularity.modules.Module;
import com.piotrak.impl.connectivity.mqtt.MQTTConnection;
import com.piotrak.impl.connectivity.mqtt.MQTTConnectionService;
import com.piotrak.impl.connectivity.visibility.VisibilityCommand;
import com.piotrak.impl.modularity.rules.Rules;
import com.piotrak.impl.types.ConnectivityType;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SmartHomeApp {
    
    public static final Logger LOGGER = Logger.getLogger(SmartHomeApp.class);
    
    public static final String CONFIG_FILE = "config.xml";
    
    public static final String CONFIG_MODULE = "modules.module";
    
    private static final String CONFIG_CONNECTION = "connections.connection";
    
    private static final String SERVER_CONFIG = "srv-cfg: ";
    
    private List<Module> moduleList = new ArrayList<>(1);
    
    private Map<ConnectivityType, IConnectionService> connectionServicesList = new EnumMap<>(ConnectivityType.class);
    
    private Rules rules;
    
    private ServerSocket serverSocket;
    
    private List<Socket> socketList = new ArrayList<>(0);
    
    public SmartHomeApp(List<HierarchicalConfiguration> moduleConfigList, List<HierarchicalConfiguration> connectionConfigList) {
        rules = new Rules(connectionServicesList, moduleList);
        try {
            serverSocket = new ServerSocket(1024);
            LOGGER.info("ServerSocket created: " + serverSocket.getLocalSocketAddress());
            loadModules(moduleConfigList);
            loadConnectionServices(connectionConfigList);
        } catch (IOException e) {
            LOGGER.error("Unable to create a ServerSocket on port 1024");
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        String config = CONFIG_FILE;
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals("-C") && args.length > i + 1) {
                    config = args[i + 1];
                }
            }
        }
        try {
            XMLConfiguration configFile = new XMLConfiguration(config);
            List<HierarchicalConfiguration> moduleConfigList = configFile.configurationsAt(CONFIG_MODULE);
            List<HierarchicalConfiguration> connectionConfigList = configFile.configurationsAt(CONFIG_CONNECTION);
            SmartHomeApp app = new SmartHomeApp(moduleConfigList, connectionConfigList);
            ServicesApp servicesApp = new ServicesApp(app.getConnectionServicesList());
            servicesApp.connect();
            do {
                Socket socket = app.serverSocket.accept();
                app.socketList.add(socket);
                SocketServerListener listener = app.new SocketServerListener(socket);
                Thread thread = new Thread(listener);
                thread.start();
            } while (true);
        } catch (ConfigurationException e) {
            LOGGER.error("Problem occurred while reading the config file: " + config + "\n", e);
        } catch (IOException e) {
            LOGGER.warn("Unable to connect new client to the server", e);
        }
    }
    
    private void loadConnectionServices(List<HierarchicalConfiguration> connectionConfigList) {
        for (HierarchicalConfiguration connectionConfig : connectionConfigList) {
            IConnectionService connectionService;
            IConnection connection;
            ConnectivityType connectivityType = ConnectivityType.valueOf(connectionConfig.getString("type"));
            if (ConnectivityType.MQTT.equals(connectivityType)) {
                connectionService = new MQTTConnectionService();
                connection = new MQTTConnection();
                ((MQTTConnection) connection).setRules(rules);
            } else {
                LOGGER.error("Connection type is incorrect");
                return;
            }
            connection.config(connectionConfig);
            connectionService.config(getModulesByCommunicationType(connectivityType), connection);
            connectionServicesList.put(connectivityType, connectionService);
            LOGGER.info("Loaded connectionService: " + connectionService.getConnectivityType());
        }
    }
    
    private List<Module> getModulesByCommunicationType(ConnectivityType connectivityType) {
        List<Module> modulesList = new ArrayList<>(1);
        moduleList.forEach(module -> {
            if (connectivityType.equals(module.getCommunication().getConnectivityType())) {
                modulesList.add(module);
            }
        });
        return modulesList;
    }
    
    private void loadModules(List<HierarchicalConfiguration> moduleConfigList) {
        for (HierarchicalConfiguration moduleConfig : moduleConfigList) {
            Module module = new Module();
            module.config(moduleConfig);
            moduleList.add(module);
            LOGGER.info("Loaded " + module.toString());
        }
        LOGGER.info(moduleList.size() + " modules loaded");
    }
    
    public List<Module> getModuleList() {
        return new ArrayList<>(moduleList);
    }
    
    public Map<ConnectivityType, IConnectionService> getConnectionServicesList() {
        return new EnumMap<>(connectionServicesList);
    }
    
    private class SocketServerListener implements Runnable {
        
        private Socket socket;
        
        private PrintWriter out = null;
        
        private BufferedReader in = null;
        
        private SocketServerListener(Socket socket) {
            this.socket = socket;
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                sendModulesToNewClient();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        private void sendModulesToNewClient() {
            moduleList.forEach(m -> {
                out.println(SERVER_CONFIG + m);
                out.flush();
            });
        }
        
        @Override
        public void run() {
            try {
                do {
                    String message = in.readLine();
                    LOGGER.info("Message received from client " + socket.getLocalAddress() + ": " + message);
                    if (message != null) {
                        String moduleName = message.substring(message.indexOf("Module=") + 7, message.indexOf(", commandText="));
                        String commandText = message.substring(message.indexOf(", commandText=") + 14, message.indexOf(", commandValue="));
                        String commandValueText = message.substring(message.indexOf(", commandValue=") + 16);
                        int commandValue = 0;
                        try {
                            commandValue = Integer.valueOf(commandValueText);
                        } catch (NumberFormatException nfe) {
                            commandValue = 0;
                        }
                        Module module = null;
                        for (Module m : moduleList) {
                            if (moduleName.equals(m.getName())) {
                                module = m;
                                break;
                            }
                        }
                        if (module == null) {
                            LOGGER.warn("Incorrect command received from the client, no such module: " + moduleName);
                        } else {
                            VisibilityCommand command = new VisibilityCommand(commandText, commandValue, module);
                            rules.act(command);
                        }
                    }
                }
                while (true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
