package Ports;

import org.w3c.dom.Document;

public class InputPort {

    private Slot input = new Slot();

    public void setinput(Slot input) {
        this.input = input;
    }

    public Slot getInput() {
        return input;
    }

    public void writeSlotInput(Document doc) throws Exception {
        input.write(doc);
    }

    public Document readSlotInput() throws Exception {
        return input.read();
    }
}
