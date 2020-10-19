package okon.Nautilus.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okon.Nautilus.Action;
import okon.Nautilus.exception.AppException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class HostConfigReader {
    public static Map<String, Map<String, ObservableList<Action>>> readParams(File file) {
        Element config = parseXml(file);
        Map<String, Map<String, ObservableList<Action>>> result = new TreeMap<>();
        NodeList firstLevelTabs = config.getChildNodes();
        if (firstLevelTabs != null && firstLevelTabs.getLength() > 0) {
            for (int i = 0; i < firstLevelTabs.getLength(); i++) {
                Node firstLevelTab = firstLevelTabs.item(i);
                if (firstLevelTab.getNodeType() == Node.ELEMENT_NODE) {
                    Map<String, ObservableList<Action>> secondLevelTabActions = new TreeMap<>();
                    NodeList secondLevelTabs = firstLevelTab.getChildNodes();
                    if (secondLevelTabs != null && secondLevelTabs.getLength() > 0) {
                        for (int j = 0; j < secondLevelTabs.getLength(); j++) {
                            Node secondLevelTab = secondLevelTabs.item(j);
                            if (secondLevelTab.getNodeType() == Node.ELEMENT_NODE) {
                                ObservableList<Action> actions = FXCollections.observableArrayList();
                                NodeList servers = secondLevelTab.getChildNodes();
                                if (servers != null && servers.getLength() > 0) {
                                    for (int k = 0; k < servers.getLength(); k++) {
                                        Node server = servers.item(k);
                                        if (server.getNodeType() == Node.ELEMENT_NODE) {
                                            Element element = (Element) server;
                                            String serverIp = element.getElementsByTagName("ip").item(0).getTextContent();
                                            Integer serverPort = Integer.valueOf(element.getElementsByTagName("port").item(0).getTextContent());
                                            String serverCommand = element.getElementsByTagName("command").item(0).getTextContent();
                                            Boolean secureMode = Boolean.valueOf(element.getElementsByTagName("command").item(0).getAttributes().item(0).getNodeValue());
                                            String serverDescription = element.getElementsByTagName("description").item(0).getTextContent();
                                            List<String> interfaceNames = new ArrayList<>();
                                            interfaceNames.add(element.getElementsByTagName("interface_name").item(0).getTextContent());
                                            interfaceNames.add(element.getElementsByTagName("interface_name").item(1).getTextContent());
                                            actions.add(new Action(serverIp, serverPort, serverCommand, secureMode, serverDescription, interfaceNames));
                                        }
                                    }
                                }
                                secondLevelTabActions.put(secondLevelTab.getNodeName(), actions);
                            }
                        }
                    }
                    result.put(firstLevelTab.getNodeName(), secondLevelTabActions);
                }
            }
        }
        return result;
    }

    private static Element parseXml(File file) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(file);
            return document.getDocumentElement();
        } catch (Exception e) {
            throw new AppException(e);
        }
    }
}
