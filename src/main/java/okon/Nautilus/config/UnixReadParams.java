package okon.Nautilus.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okon.Nautilus.Action;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UnixReadParams {
    public static List<ObservableList<Action>> readServers(Element root) {
        List<ObservableList<Action>> result = new ArrayList<>();

        NodeList subtabs = root.getElementsByTagName("Unix");

        if (subtabs != null && subtabs.getLength() > 0) {
            for (int i = 0; i < subtabs.getLength(); i++) {
                ObservableList<Action> actions = FXCollections.observableArrayList();

                Node subtab = subtabs.item(i);
                NodeList servers = subtab.getChildNodes();

                for (int j = 0; j < servers.getLength(); j++) {
                    Node server = servers.item(j);

                    if (server.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) server;

                        String serverIp = element.getElementsByTagName("srv_ip").item(0).getTextContent();
                        Integer serverPort = Integer.valueOf(element.getElementsByTagName("srv_port").item(0).getTextContent());
                        String serverCommand = element.getElementsByTagName("srv_cmd").item(0).getTextContent();
                        String serverDescription = element.getElementsByTagName("srv_desc").item(0).getTextContent();

                        List<String> interfaceNames = new ArrayList<>();
                        interfaceNames.add(element.getElementsByTagName("interface_name").item(0).getTextContent());
                        interfaceNames.add(element.getElementsByTagName("interface_name").item(1).getTextContent());

                        actions.add(new Action(serverIp, serverPort, serverCommand, serverDescription, interfaceNames));
                    }
                }

                result.add(actions);
            }
        }

        return result;
    }
}
