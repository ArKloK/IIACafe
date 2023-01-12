package Ports;

import java.util.LinkedList;
import java.util.Queue;
import org.w3c.dom.Document;

public class Slot {

    private final Queue<Document> testQueue = new LinkedList();

    public Queue getQueue() {
        return testQueue;
    }

    public Document read() throws Exception {
        return testQueue.remove();
    }

    public void write(Document doc) throws Exception {
        testQueue.add(doc);
    }
}
