package Tasks;

import Ports.Slot;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class Aggregator {

    private Slot input;
    private Slot output;

    public Aggregator(Slot input, Slot output) {
        this.input = input;
        this.output = output;
    }

    public void run(String id_order) throws ParserConfigurationException, Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //root element
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("cafe_order");
        doc.appendChild(rootElement);

        //order_id
        Element order_id = doc.createElement("order_id");
        Text nodeOrderIdValue = doc.createTextNode(id_order);
        order_id.appendChild(nodeOrderIdValue);
        rootElement.appendChild(order_id);

        //drinks
        Element drinks = doc.createElement("drinks");
        rootElement.appendChild(drinks);

        int numDrinks = input.getQueue().size();

        for (int i = 0; i < numDrinks; i++) //For each drink...
        {
            Document d = input.read();

            //We get the name and stock...
            d.getDocumentElement().normalize();
            NodeList nList = d.getElementsByTagName("name");
            NodeList nList2 = d.getElementsByTagName("stock");
            String name = nList.item(0).getTextContent();//name
            String available = nList2.item(0).getTextContent();//stock

            //We create a drink...
            Element drink = doc.createElement("drink");
            drinks.appendChild(drink);

            //Adding name field...
            Element name2 = doc.createElement("name");
            Text nodeNameValue = doc.createTextNode(name);
            name2.appendChild(nodeNameValue);
            drink.appendChild(name2);

            //Adding stock field (yes/no)
            Element stock = doc.createElement("stock");
            Text nodeStockValue = doc.createTextNode(available);
            stock.appendChild(nodeStockValue);
            drink.appendChild(stock);
        }

        output.write(doc); //We write on the output slot
    }
}
