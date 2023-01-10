package Ports;

import org.w3c.dom.Document;

public class OutputPort {

    private Slot output;

    public void writeSlotOutput(Document elemento) throws Exception {
        output.write(elemento);
    }

    public void doWork(Document elemento) throws Exception {
        this.writeSlotOutput(elemento);
    }
}
