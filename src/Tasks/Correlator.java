package Tasks;

import Ports.Slot;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Correlator {

    private Slot inputDB;
    private Slot inputReplicator;
    private Slot output1;
    private Slot output2;

    public Correlator(Slot inputDB, Slot inputReplicator, Slot output1, Slot output2) {
        this.inputDB = inputDB;
        this.inputReplicator = inputReplicator;
        this.output1 = output1;
        this.output2 = output2;
    }

    public Slot getInputDB() {
        return inputDB;
    }

    public Slot getInputReplicator() {
        return inputReplicator;
    }

    public Slot getOutput1() {
        return output1;
    }

    public Slot getOutput2() {
        return output2;
    }

    public void setInputDB(Slot inputDB) {
        this.inputDB = inputDB;
    }

    public void setInputReplicator(Slot inputReplicator) {
        this.inputReplicator = inputReplicator;
    }

    public void setOutput1(Slot output1) {
        this.output1 = output1;
    }

    public void setOutput2(Slot output2) {
        this.output2 = output2;
    }

    public void run() throws Exception {
        int counter = inputDB.getQueue().size();

        for (int i = 0; i < counter; i++) {
            Document docDB = inputDB.read();
            Document docRep = inputReplicator.read();

            //Cogemos el nombre del input de la base de datos
            XPath xPath = XPathFactory.newInstance().newXPath();
            NodeList drinkDB = (NodeList) xPath.compile("//name").evaluate(docDB, XPathConstants.NODESET);
            Node D = drinkDB.item(0);
            Element d = (Element) D;
            String nameDB = d.getTextContent();

            //Cogemos el nombre del input del replicator
            XPath xPath2 = XPathFactory.newInstance().newXPath();
            NodeList drinkRep = (NodeList) xPath.compile("//name").evaluate(docRep, XPathConstants.NODESET);
            Node R = drinkRep.item(0);
            Element r = (Element) R;
            String nameRep = r.getTextContent();

            //Devuelve true si los Strings son iguales, ignorando mayusculas y minusculas, y los escribimos en las salidas
            if (nameDB.equalsIgnoreCase(nameRep)) {
                output1.write(docDB);
                output2.write(docRep);
            }
        }
    }
}
