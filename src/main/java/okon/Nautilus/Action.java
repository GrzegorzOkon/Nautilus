package okon.Nautilus;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.List;

public class Action {
    private final SimpleStringProperty ip;
    private final SimpleIntegerProperty port;
    private final SimpleStringProperty command;
    private final SimpleStringProperty description;
    private final List<String> interfaceNames;

    public Action(String ip, Integer port, String command, String description, List<String> interfaceNames) {
        this.ip = new SimpleStringProperty(ip);
        this.port = new SimpleIntegerProperty(port);
        this.command = new SimpleStringProperty(command);
        this.description = new SimpleStringProperty(description);
        this.interfaceNames = interfaceNames;
    }

    public String getIp() { return ip.get(); }

    public Integer getPort() { return port.get(); }

    public String getCommand() {
        return command.get();
    }

    public String getDescription() {
        return description.get();
    }

    public List<String> getInterfaceNames() { return interfaceNames; }
}