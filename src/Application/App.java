package Application;

import Connectors.*;
import Tasks.*;
import Ports.*;
import java.util.Scanner;
import javax.xml.transform.TransformerException;

public class App {

    public static void main(String[] args) throws Exception {
        InputConnector ic = new InputConnector();  //INPUT CONNECTOR
        InputPort ip = new InputPort();     //INPUT PORT

        //COMMON SLOTS BETWEEN COLD AND HOT DRINKS:
        Slot SplitterToDistributor = new Slot();
        Slot slotOutput1Distributor = new Slot(); //Hot drinks come out here
        Slot slotOutput2Distributor = new Slot(); //Cold drinks come out here

        //SLOTS HOT DRINKS:
        Slot slotOutputReplicatorHotDrinksToTranslator = new Slot();
        Slot slotOutputReplicatorHotDrinksToCorrelator = new Slot();
        Slot slotOutputTranslatorHotDrinks = new Slot();
        Slot slotOutput1CorrelatorHotDrinks = new Slot();
        Slot slotOutput2CorrelatorHotDrinks = new Slot();
        Slot slotQueryHotDrinks = new Slot();
        Slot slotOutputEnricherHotDrinks = new Slot();

        //SLOTS COLD DRINKS:
        Slot slotOutputReplicatorColdDrinksToTranslator = new Slot();
        Slot slotOutputReplicatorColdDrinksToCorrelator = new Slot();
        Slot slotOutputTranslatorColdDrinks = new Slot();
        Slot slotOutput1CorrelatorColdDrinks = new Slot();
        Slot slotOutput2CorrelatorColdDrinks = new Slot();
        Slot slotQueryColdDrinks = new Slot();
        Slot slotOutputEnricherColdDrinks = new Slot();

        //OTHER COMMON SLOTS:
        Slot slotMergerToAgregator = new Slot();
        Slot slotOutputAggregator = new Slot();

        RequestPort rPortHotDrinks = new RequestPort(slotOutputTranslatorHotDrinks, slotQueryHotDrinks); //REQUEST PORT HOT DRINKS
        RequestPort rPortColdDrinks = new RequestPort(slotOutputTranslatorColdDrinks, slotQueryColdDrinks); //REQUEST PORT COLD DRINKS

        //COMMON TASK DEFINITION:
        Splitter ts = new Splitter(ip.getInput(), SplitterToDistributor);
        Distributor td = new Distributor(SplitterToDistributor, slotOutput1Distributor, slotOutput2Distributor);

        //HOT DRINKS TASKS DEFINITION:
        Replicator trHotDrinks = new Replicator(slotOutput1Distributor, slotOutputReplicatorHotDrinksToTranslator, slotOutputReplicatorHotDrinksToCorrelator);
        Translator ttHotDrinks = new Translator(slotOutputReplicatorHotDrinksToTranslator, slotOutputTranslatorHotDrinks);
        Correlator tcHotDrinks = new Correlator(slotQueryHotDrinks, slotOutputReplicatorHotDrinksToCorrelator, slotOutput1CorrelatorHotDrinks, slotOutput2CorrelatorHotDrinks);
        ContextEnricher teHotDrinks = new ContextEnricher(slotOutput1CorrelatorHotDrinks, slotOutput2CorrelatorHotDrinks, slotOutputEnricherHotDrinks);

        //COLD DRINKS TASKS DEFINITION:
        Replicator trColdDrinks = new Replicator(slotOutput2Distributor, slotOutputReplicatorColdDrinksToTranslator, slotOutputReplicatorColdDrinksToCorrelator);
        Translator ttColdDrinks = new Translator(slotOutputReplicatorColdDrinksToTranslator, slotOutputTranslatorColdDrinks);
        Correlator tcColdDrinks = new Correlator(slotQueryColdDrinks, slotOutputReplicatorColdDrinksToCorrelator, slotOutput1CorrelatorColdDrinks, slotOutput2CorrelatorColdDrinks);
        ContextEnricher teColdDrinks = new ContextEnricher(slotOutput1CorrelatorColdDrinks, slotOutput2CorrelatorColdDrinks, slotOutputEnricherColdDrinks);

        //OTHER COMMON TASKS DEFINITION:
        Merger tm = new Merger(slotOutputEnricherHotDrinks, slotOutputEnricherColdDrinks, slotMergerToAgregator);
        Aggregator ta = new Aggregator(slotMergerToAgregator, slotOutputAggregator);

        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Write the name of the XML command document: ");
            String fichero = sc.nextLine();
            String order_id = ic.run(fichero); //parses file XML to document
            ip.writeSlotInput(ic.getDocument()); //write the initial document on the input slot 

            //--------------- COMMON TASK COLD AND HOT DRINKS ------------
            //TASK SPLITTER:
            System.out.println("******************************************");
            System.out.println("Running Splitter task...");
            ts.run();
            //TASK DISTRIBUTOR:
            System.out.println("Running Distributor task...");
            System.out.println("******************************************");
            td.run();

            //---------------------------- HOT DRINKS ----------------------
            //TASK REPLICATOR HOT DRINKS:
            System.out.println("Running Replicator task for hot drinks...");
            trHotDrinks.run();
            trHotDrinks.generaXML(slotOutputReplicatorHotDrinksToTranslator, slotOutputReplicatorHotDrinksToCorrelator);//generates the xml needed for the translator
            //TASK TRANSLATOR HOT DRINKS:
            System.out.println("Running Translator for hot drinks...");
            ttHotDrinks.run();
            //QUERY DB HOT DRINKS:
            System.out.println("Hot drinks barman preparing drinks, querying stock...");
            rPortHotDrinks.doWork();
            //TASK CORRELATOR HOT DRINKS:
            System.out.println("Running Correlator task for hot drinks...");
            tcHotDrinks.run();
            //TASK ENRICHER HOT DRINKS:
            System.out.println("Running Context Enricher task for hot drinks...");
            teHotDrinks.run();

            //---------------------------- COLD DRINKS --------------------------
            //TASK REPLICATOR COLD DRINKS
            System.out.println("******************************************");
            System.out.println("Running Replicator task for cold drinks...");
            trColdDrinks.run();
            trColdDrinks.generaXML(slotOutputReplicatorColdDrinksToTranslator, slotOutputReplicatorColdDrinksToCorrelator); //generates the xml needed for the translator
            //TASK TRANSLATOR COLD DRINKS:
            System.out.println("Running Translator for cold drinks...");
            ttColdDrinks.run();
            //QUERY DB COLD DRINKS:
            System.out.println("Cold drinks barman preaparing drinks, queriying stock...");
            rPortColdDrinks.doWork();
            //TASK CORRELATOR COLD DRINKS:
            System.out.println("Running Correlator task for cold drinks...");
            tcColdDrinks.run();
            //TASK CONTEXT ENRICHER COLD DRINKS:
            System.out.println("Running Context Enricher task for cold drinks...");
            teColdDrinks.run();

            //--------------------------- OTHER COMMON TASKS --------------------
            //TASK MERGER:
            System.out.println("******************************************");
            System.out.println("Running Merger task...");
            tm.run();
            //TASK AGGREGATOR:
            System.out.println("Running Aggregator task...");
            ta.run("1");

            //TO OUTPUT CONNECTOR:
            System.out.println("******************************************");
            System.out.println("Write the name of the output XML file:");
            String f = sc.nextLine();
            OutputConnector cs = new OutputConnector(slotOutputAggregator.read());
            cs.generate(f);

        } catch (TransformerException ex) {
        }
    }
}
