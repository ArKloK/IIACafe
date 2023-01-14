package Tasks;

import org.w3c.dom.Document;
import Ports.Slot;
import Connectors.OutputConnector;

public class Replicator {

    private Slot input;
    private Slot output1;
    private Slot output2;

    public Replicator(Slot input, Slot output1, Slot output2) {
        this.input = input;
        this.output1 = output1;
        this.output2 = output2;
    }

    public Slot getInput() {
        return input;
    }

    public void setInput(Slot input) {
        this.input = input;
    }

    public Slot getOutput1() {
        return output1;
    }

    public Slot getOutput2() {
        return output2;
    }

    public void setOutput1(Slot output1) {
        this.output1 = output1;
    }

    public void setOutput2(Slot output2) {
        this.output2 = output2;
    }

    public void run() throws Exception {
        int counter = input.getQueue().size();

        for (int i = 0; i < counter; i++) {
            Document doc = input.read();//Leemos el el documento del slot de entrada
            //Colocamos el documento en cada salida
            output1.write(doc);
            output2.write(doc);
        }
    }

    public void generaXML(Slot slot1, Slot slot2) throws Exception {
        int n = slot1.getQueue().size();

        for (int i = 0; i < n; i++) {
            Document doc = slot1.read();
            slot1.write(doc);
            OutputConnector cs = new OutputConnector(doc);
            cs.generate("input" + i);
        }
    }
}
