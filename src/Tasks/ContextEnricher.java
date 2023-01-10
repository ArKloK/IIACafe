package Tasks;

import Ports.Slot;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ContextEnricher {

    private Slot input1;
    private Slot input2;
    private Slot output;

    public ContextEnricher(Slot input1, Slot input2, Slot output) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public Slot getInput1() {
        return input1;
    }

    public void setInput1(Slot input1) {
        this.input1 = input1;
    }

    public Slot getInput2() {
        return input2;
    }

    public void setInput2(Slot input2) {
        this.input2 = input2;
    }

    public Slot getOutput() {

        return output;
    }

    public void setOutput(Slot output) {
        this.output = output;
    }

    public void run() throws Exception {
        int counter = input1.getQueue().size();

        for (int i = 0; i < counter; i++) {
            Document doc1 = input1.read();
            Document doc2 = input2.read();

            Element e1 = doc1.getDocumentElement();
            Element e2 = doc2.getDocumentElement();

            Node node;

            while ((node = e1.getFirstChild()) != null) {
                doc2.adoptNode(node);
                e2.appendChild(node);
            }

            output.write(doc2);
        }
    }
}
