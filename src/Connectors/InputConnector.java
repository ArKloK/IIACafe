package Connectors;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class InputConnector {

    private DocumentBuilderFactory dbFactory;
    private DocumentBuilder dBuilder;
    private Document doc;

    public String run(String file) {
        String value = "";
        String f = file;

        if (!f.endsWith(".xml")) {
            f = f + ".xml";
        }

        try {
            File inputFile = new File(f);
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(inputFile);
        } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
        }

        return value;
    }

    public Document getDocument() {
        return doc;
    }
}
