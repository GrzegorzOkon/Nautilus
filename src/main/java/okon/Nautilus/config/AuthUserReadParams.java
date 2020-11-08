package okon.Nautilus.config;

import okon.Nautilus.exception.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class AuthUserReadParams {
    private static final Logger logger = LogManager.getLogger(AuthUserReadParams.class);

    public static Map<String, List<String>> readParams(File file) {
        Map<String, List<String>> result = new HashMap<>();
        try {
            Element config = parseXml(file);
            NodeList aUsers = config.getElementsByTagName("auth_user");
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
        } catch (Exception e) {
            logger.error("ParseXml(" + file.getName() + ") : " + e.getMessage());
            throw new ConfigurationException(e.getMessage());
        }
        logger.debug("ReadParams(" + file.getName() + ") : OK");
        return result;
    }

    private static Element parseXml(File file) throws Exception {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(file);
        return document.getDocumentElement();
    }
}
