package Tasks;

import Ports.Slot;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Splitter {

    private Slot input;
    private Slot output;
    private NodeList drinks;

    public Splitter(Slot input, Slot output) {
        this.input = input;
        this.output = output;
    }

    public Slot getInput() {
        return input;
    }

    public Slot getOutput() {
        return output;
    }

    public void setInput(Slot input) {
        this.input = input;
    }

    public void setOutput(Slot output) {
        this.output = output;
    }

    public void run() throws Exception {
        Document doc = input.read(); //read the input slot
        XPath xPath = XPathFactory.newInstance().newXPath();
        drinks = (NodeList) xPath.compile("//drinks/*").evaluate(doc, XPathConstants.NODESET);
        NodeList order = doc.getElementsByTagName("order_id");
        Node ord = order.item(0);

        for (int i = 0; i < drinks.getLength(); i++) //For each node
        {
            Document doc2 = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Node b = drinks.item(i);

            if (b.getNodeType() == Node.ELEMENT_NODE) {
                Node drink = drinks.item(i);

                if (drink.getNodeType() == Node.ELEMENT_NODE) {
                    Node copyNode = doc2.importNode(drink, true);
                    doc2.appendChild(copyNode);
                    Node TagOrder = doc2.importNode(ord, true);
                    doc2.getDocumentElement().appendChild(TagOrder);
                }
            }

            output.write(doc2); //write on output slot
        }
    }
}
