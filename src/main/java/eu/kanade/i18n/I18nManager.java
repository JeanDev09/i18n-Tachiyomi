package eu.kanade.i18n;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author jeandev
 */
public class I18nManager {

    private Map<String, String> strings;
    private Map<String, Map<String, String>> plurals;

    public I18nManager(Locale locale) {
        loadStrings(locale);
        loadPlurals(locale);
    }

    private void loadPlurals(Locale locale) {
        plurals = new HashMap<>();
        try {
            File xmlFile = new File("src/main/resources/MR/" + locale.getLanguage() + "/plurals.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("plurals");
            for (int i = 0; i < nList.getLength(); i++) {
                Element pluralElement = (Element) nList.item(i);
                String name = pluralElement.getAttribute("name");
                Map<String, String> pluralMap = new HashMap<>();
                
                NodeList itemList = pluralElement.getElementsByTagName("item");
                for (int j = 0; j < itemList.getLength(); j++) {
                    Element iteElement = (Element) itemList.item(j);
                    pluralMap.put(iteElement.getAttribute("quantity"), iteElement.getTextContent());
                }
                plurals.put(name, pluralMap);
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
            System.err.println("Error al cargar plurals: " + e.getMessage());
        }
    }

    private void loadStrings(Locale locale) {
        strings = new HashMap<>();
        try {
            File xmlFile = new File("src/main/resources/MR/" + locale.getLanguage() + "/strings.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dbuilder = dbFactory.newDocumentBuilder();
            Document doc = dbuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("string");
            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                strings.put(element.getAttribute("name"), element.getTextContent());
            }
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
            System.err.println("Error al cargar Strings: " + e.getMessage());
        }
    }

    public String getString(String key){
        return strings.getOrDefault(key, key);
    }

   public String getPlural(String key, int quantity){
       Map<String, String> pluralMap = plurals.get(key);
        if (pluralMap != null) {
            if (quantity == 1) {
                return pluralMap.get("one");
            } else {
                return pluralMap.get("other");
            }
        }
        return key;
   }
    
}
