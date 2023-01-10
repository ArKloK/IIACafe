package Connectors;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class OutputConnector {

    private String file_name;
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private DOMImplementation implementation;
    private Document document;
    private Element root;

    public OutputConnector(Document docu) {
        this.document = docu;
    }

    public OutputConnector(String name) {
        try {
            this.builder = factory.newDocumentBuilder();
            this.implementation = builder.getDOMImplementation();
            this.file_name = name;
            this.document = implementation.createDocument(null, name, null);
            this.document.setXmlVersion("1.0");
            this.root = document.getDocumentElement();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(OutputConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generate(String name) throws TransformerException {
        //Generates XML
        Source source = new DOMSource(document);

        //We set where we want to store it
        Result result = new StreamResult(new java.io.File(name + ".xml")); //File name
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);
    }
}
