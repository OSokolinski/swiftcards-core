package swiftcards.core.util;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.util.HashMap;

public class TextService {

    private static TextService instance;
    private final Document document;
    private final String lang = "en";

    private final HashMap<String, String> generalTexts;
    private final HashMap<String, String> translatedTexts;

    public static TextService getInstance() {
        if (instance == null) {
            try {
                instance = new TextService();
            }
            catch (Exception e) {
                System.out.println("XML Error: " + e);
            }
        }

        return instance;
    }

    private TextService() throws Exception {
        File file = new File("lib/src/main/resources/cli.xml");
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        document = documentBuilder.parse(file);

        NodeList generalTextNodes = document.getElementsByTagName("text-general").item(0).getChildNodes();
        NodeList translatedTextNodes = document.getElementsByTagName("text-translations").item(0).getChildNodes();

        generalTexts = decodeNode(generalTextNodes, false);
        translatedTexts = decodeNode(translatedTextNodes, true);
    }

    private HashMap<String, String> decodeNode(NodeList nodeList, boolean isTranslatedText) {
        HashMap<String, String> map = new HashMap<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element element = (Element) node;
            String textId = element.getAttribute("id");

            if (!isTranslatedText) {
                map.put(textId, node.getTextContent());
                continue;
            }

            NodeList translatedNodes = node.getChildNodes();

            for (int j = 0; j < translatedNodes.getLength(); j++) {
                Node translatedNode = translatedNodes.item(j);

                if (translatedNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                if (translatedNode.getNodeName().equals(lang)) {
                    map.put(textId, translatedNode.getTextContent());
                }
            }
        }

        return map;
    }

    public String getGeneralText(String id) {
        return generalTexts.get(id);
    }

    public String getTranslatedText(String id) {
        return translatedTexts.get(id);
    }

}
