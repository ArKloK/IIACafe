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

    private final Slot input;
    private final Slot output;

    public Aggregator(Slot input, Slot output) {
        this.input = input;
        this.output = output;
    }

    public void run(String id_order) throws ParserConfigurationException, Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        //elemento raiz
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("cafe_order");
        doc.appendChild(rootElement);

        //order_id
        Element order_id = doc.createElement("order_id");
        Text nodeOrderIdValue = doc.createTextNode(id_order);
        order_id.appendChild(nodeOrderIdValue);
        rootElement.appendChild(order_id);

        //bebidas
        Element drinks = doc.createElement("drinks");
        rootElement.appendChild(drinks);

        int numDrinks = input.getQueue().size();

        for (int i = 0; i < numDrinks; i++)
        {
            Document d = input.read();

            //Obtenemos el nombre y el stock
            d.getDocumentElement().normalize();
            NodeList nList = d.getElementsByTagName("name");
            NodeList nList2 = d.getElementsByTagName("stock");
            String name = nList.item(0).getTextContent();//name
            String available = nList2.item(0).getTextContent();//stock

            //Creamos bebida
            Element drink = doc.createElement("drink");
            drinks.appendChild(drink);

            //Añadimos el campo nombre
            Element name2 = doc.createElement("name");
            Text nodeNameValue = doc.createTextNode(name);
            name2.appendChild(nodeNameValue);
            drink.appendChild(name2);

            //Añadimos el campo stock
            Element stock = doc.createElement("stock");
            Text nodeStockValue = doc.createTextNode(available);
            stock.appendChild(nodeStockValue);
            drink.appendChild(stock);
        }

        output.write(doc); //Escribimos en el slot de salida
    }
}
