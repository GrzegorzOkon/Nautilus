package okon.Nautilus.config;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

public class AuthUserReadParams {
    public static Map<String, List<String>> readAuthUsers(Element root) {
        Map<String, List<String>> result = new HashMap<>();

        NodeList aUsers = root.getElementsByTagName("auth_user");

        if (aUsers != null && aUsers.getLength() > 0) {
            for (int i = 0; i < aUsers.getLength(); i++) {
                Node aUser = aUsers.item(i);

                if (aUser.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) aUser;

                    String interfaceName = element.getElementsByTagName("interface_name").item(0).getTextContent();
                    String user = element.getElementsByTagName("user").item(0).getTextContent();
                    String password = element.getElementsByTagName("password").item(0).getTextContent();
                    String domain = element.getElementsByTagName("domain").item(0).getTextContent();

                    result.put(interfaceName, Arrays.asList(user, password, domain));
                }
            }
        }

        return result;
    }
}
