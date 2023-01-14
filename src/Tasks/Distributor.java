package Tasks;

import Ports.Slot;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Distributor {

    //Necesita dos slots de entrada
    private Slot input;
    private Slot output1;
    private Slot output2;

    public Distributor(Slot input, Slot output1, Slot output2) {
        this.input = input;
        this.output1 = output1;
        this.output2 = output2;
    }

    public Slot getInput() {
        return input;
    }

    public Slot getOutput1() {
        return output1;
    }

    public Slot getOutput2() {
        return output2;
    }

    public void setInput(Slot input) {
        this.input = input;
    }

    public void setOutput1(Slot output1) {
        this.output1 = output1;
    }

    public void setOutput2(Slot output2) {
        this.output2 = output2;
    }

    public void run() throws Exception {
        try {
            int counter = input.getQueue().size();

            for (int i = 0; i < counter; i++) {
                Document doc = input.read();
                XPath xPath = XPathFactory.newInstance().newXPath();
                NodeList nodo = (NodeList) xPath.compile("/drink/type").evaluate(doc, XPathConstants.NODESET);

                Node drk = nodo.item(0);

                if (drk.getNodeType() == Node.ELEMENT_NODE) {
                    Element type = (Element) drk;

                    if (type.getTextContent().equalsIgnoreCase("hot")) {
                        output1.write(doc);
                    } else {
                        output2.write(doc);
                    }
                }
            }
        } catch (XPathExpressionException ex) {
            Logger.getLogger(Distributor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
