package okon.Nautilus.config;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import okon.Nautilus.Action;
import okon.Nautilus.exception.ConfigurationException;
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
        if (areTabsPresent(firstLevelTabs)) {
            for (int i = 0; i < firstLevelTabs.getLength(); i++) {
                Node firstLevelTab = firstLevelTabs.item(i);
                if (isElementNode(firstLevelTab)) {
                    Map<String, ObservableList<Action>> secondLevelTabsActions = new TreeMap<>();
                    NodeList secondLevelTabs = firstLevelTab.getChildNodes();
                    if (areTabsPresent(secondLevelTabs)) {
                        for (int j = 0; j < secondLevelTabs.getLength(); j++) {
                            Node secondLevelTab = secondLevelTabs.item(j);
                            if (isElementNode(secondLevelTab)) {
                                secondLevelTabsActions.put(secondLevelTab.getNodeName(), createActionsFromTab(secondLevelTab));
                            }
                        }
                    }
                    result.put(firstLevelTab.getNodeName(), secondLevelTabsActions);
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
            throw new ConfigurationException(e.getMessage());
        }
    }

    private static boolean areTabsPresent(NodeList tabs) {
        if (tabs != null && tabs.getLength() > 0) {
            return true;
        }
        return false;
    }

    private static boolean isElementNode(Node tab) {
        if (tab.getNodeType() == Node.ELEMENT_NODE) {
            return true;
        }
        return false;
    }

    private static ObservableList<Action> createActionsFromTab(Node tab) {
        ObservableList<Action> result = FXCollections.observableArrayList();
        NodeList servers = tab.getChildNodes();
        if (servers != null && servers.getLength() > 0) {
            for (int i = 0; i < servers.getLength(); i++) {
                Node server = servers.item(i);
                if (isElementNode(server)) {
                    result.add(createAction(server));
                }
            }
        }
        return result;
    }

    private static Action createAction(Node node) {
        Element element = (Element) node;
        String hostname = element.getElementsByTagName("hostname").item(0).getTextContent();
        String serverIp = element.getElementsByTagName("ip").item(0).getTextContent();
        Integer serverPort = Integer.valueOf(element.getElementsByTagName("port").item(0).getTextContent());
        String serverCommand = element.getElementsByTagName("command").item(0).getTextContent();
        Boolean secureMode = Boolean.valueOf(element.getElementsByTagName("command").item(0).getAttributes().item(0).getNodeValue());
        String serverDescription = element.getElementsByTagName("description").item(0).getTextContent();
        List<String> interfaceNames = new ArrayList<>();
        interfaceNames.add(element.getElementsByTagName("interface_name").item(0).getTextContent());
        interfaceNames.add(element.getElementsByTagName("interface_name").item(1).getTextContent());
        return new Action(hostname, serverIp, serverPort, serverCommand, secureMode, serverDescription, interfaceNames);
    }
}
