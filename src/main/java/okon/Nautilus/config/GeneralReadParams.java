package okon.Nautilus.config;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class GeneralReadParams {
    public static Map<String, List<String>> readTabs(Element root) {
        Map<String, List<String>> result = new LinkedHashMap<>();
        NodeList children = root.getChildNodes();

        if (children != null && children.getLength() > 0) {
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    String tab = child.getNodeName();
                    List<String> subtabs = new ArrayList<>();

                    NodeList grandchildren = child.getChildNodes();

                    if (grandchildren != null && grandchildren.getLength() > 0) {
                        for (int j = 0; j < grandchildren.getLength(); j++) {
                            Node grandchild = grandchildren.item(j);

                            if (grandchild.getNodeType() == Node.ELEMENT_NODE) {
                                subtabs.add(grandchild.getNodeName());
                            }
                        }
                    }

                    result.put(tab, subtabs);
                }
            }
        }

        return result;
    }
}