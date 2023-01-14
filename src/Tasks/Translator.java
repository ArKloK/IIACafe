package Tasks;

import Connectors.InputConnector;
import Ports.Slot;
import java.io.File;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class Translator {

    private final Slot input;
    private final Slot output;

    public Translator(Slot input, Slot output) {
        this.input = input;
        this.output = output;
    }

    public void run() throws TransformerConfigurationException, TransformerException, Exception {
        InputConnector ic;
        int t = input.getQueue().size();

        for (int i = 0; i < t; i++) {
            ic = new InputConnector();
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(new File("transform.xsl"));
            Transformer transformer = factory.newTransformer(xslt);
            Source text = new StreamSource(new File("input" + i + ".xml"));
            File outputTransformation = new File("output" + i + ".xml");
            transformer.transform(text, new StreamResult(outputTransformation));
            ic.run("output" + i + ".xml");
            output.write(ic.getDocument());
        }
    }
}
