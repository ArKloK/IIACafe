package Tasks;

import Ports.Slot;

public class Merger {

    private final Slot input1;
    private final Slot input2;
    private final Slot output;

    public Merger(Slot input1, Slot input2, Slot output) {
        this.input1 = input1;
        this.input2 = input2;
        this.output = output;
    }

    public void run() throws Exception {
        int numInput1 = input1.getQueue().size();
        int numInput2 = input2.getQueue().size();

        for (int i = 0; i < numInput1; i++) {
            output.write(input1.read());
        }

        for (int i = 0; i < numInput2; i++) {
            output.write(input2.read());
        }
    }
}
