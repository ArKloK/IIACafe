package Ports;

import Connectors.ConnectorDB;
import Connectors.OutputConnector;
import org.w3c.dom.Document;

public class RequestPort {

    private final Slot input;
    private final Slot output;
    ConnectorDB conn;

    public RequestPort(Slot input, Slot output) {
        this.input = input;
        this.output = output;
    }

    public Document readInputSlot() throws Exception {
        return input.read();
    }

    public void writeOutputSlot(Document element) throws Exception {
        output.write(element);
    }

    public void doWork() throws Exception {
        Document element;
        int num = input.getQueue().size();

        for (int i = 0; i < num; i++) {
            conn = new ConnectorDB();
            element = conn.lookCommand(readInputSlot());

            OutputConnector oc = new OutputConnector(element); //Change document to obtained in last output on last task
            //oc.generate("TESTOutputDBFile.xml");

            this.writeOutputSlot(element);
        }
    }
}
