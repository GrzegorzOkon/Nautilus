package okon.Nautilus;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class Action {
    private final SimpleStringProperty hostname;
    private final SimpleStringProperty ip;
    private final SimpleIntegerProperty port;
    private final SimpleStringProperty command;
    private final SimpleBooleanProperty secureMode;
    private final SimpleStringProperty description;
    private final List<String> interfaceNames;

    public Action(String hostname, String ip, Integer port, String command, Boolean secureMode, String description, List<String> interfaceNames) {
        this.hostname = new SimpleStringProperty(hostname);
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleIntegerProperty(port);
        this.command = new SimpleStringProperty(command);
        this.secureMode = new SimpleBooleanProperty(secureMode);
        this.description = new SimpleStringProperty(description);
        this.interfaceNames = interfaceNames;
    }

    public String getHostname() { return hostname.get(); }

    public String getIp() {
        return ip.get();
    }

    public int getPort() {
        return port.get();
    }

    public String getCommand() {
        return command.get();
    }

    public boolean isSecureMode() {
        return secureMode.get();
    }

    public String getDescription() {
        return description.get();
    }

    public List<String> getInterfaceNames() {
        return interfaceNames;
    }
}