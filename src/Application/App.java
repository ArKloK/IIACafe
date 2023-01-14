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

        //SLOTS COMUNES ENTRE BEBIDAS CALIENTES Y FRIAS:
        Slot SplitterToDistributor = new Slot();
        Slot slotOutput1Distributor = new Slot(); //Las bebidas calientes van por este slot
        Slot slotOutput2Distributor = new Slot(); //Las bebidas frias van por este slot

        //SLOTS BEBIDAS CALIENTES:
        Slot slotOutputReplicatorHotDrinksToTranslator = new Slot();
        Slot slotOutputReplicatorHotDrinksToCorrelator = new Slot();
        Slot slotOutputTranslatorHotDrinks = new Slot();
        Slot slotOutput1CorrelatorHotDrinks = new Slot();
        Slot slotOutput2CorrelatorHotDrinks = new Slot();
        Slot slotQueryHotDrinks = new Slot();
        Slot slotOutputEnricherHotDrinks = new Slot();

        //SLOTS BEBIDAS FRIAS:
        Slot slotOutputReplicatorColdDrinksToTranslator = new Slot();
        Slot slotOutputReplicatorColdDrinksToCorrelator = new Slot();
        Slot slotOutputTranslatorColdDrinks = new Slot();
        Slot slotOutput1CorrelatorColdDrinks = new Slot();
        Slot slotOutput2CorrelatorColdDrinks = new Slot();
        Slot slotQueryColdDrinks = new Slot();
        Slot slotOutputEnricherColdDrinks = new Slot();

        //SLOTS COMUNES FINALES:
        Slot slotMergerToAgregator = new Slot();
        Slot slotOutputAggregator = new Slot();

        RequestPort rPortHotDrinks = new RequestPort(slotOutputTranslatorHotDrinks, slotQueryHotDrinks); //REQUEST PORT BEBIDAS CALIENTES
        RequestPort rPortColdDrinks = new RequestPort(slotOutputTranslatorColdDrinks, slotQueryColdDrinks); //REQUEST PORT BEBIDA FRIAS

        //TAREAS COMUNES:
        Splitter ts = new Splitter(ip.getInput(), SplitterToDistributor);
        Distributor td = new Distributor(SplitterToDistributor, slotOutput1Distributor, slotOutput2Distributor);

        //TAREAS BEBIDAS CALIENTES:
        Replicator trHotDrinks = new Replicator(slotOutput1Distributor, slotOutputReplicatorHotDrinksToTranslator, slotOutputReplicatorHotDrinksToCorrelator);
        Translator ttHotDrinks = new Translator(slotOutputReplicatorHotDrinksToTranslator, slotOutputTranslatorHotDrinks);
        Correlator tcHotDrinks = new Correlator(slotQueryHotDrinks, slotOutputReplicatorHotDrinksToCorrelator, slotOutput1CorrelatorHotDrinks, slotOutput2CorrelatorHotDrinks);
        ContextEnricher teHotDrinks = new ContextEnricher(slotOutput1CorrelatorHotDrinks, slotOutput2CorrelatorHotDrinks, slotOutputEnricherHotDrinks);

        //TAREAS BEBIDAS FRIAS:
        Replicator trColdDrinks = new Replicator(slotOutput2Distributor, slotOutputReplicatorColdDrinksToTranslator, slotOutputReplicatorColdDrinksToCorrelator);
        Translator ttColdDrinks = new Translator(slotOutputReplicatorColdDrinksToTranslator, slotOutputTranslatorColdDrinks);
        Correlator tcColdDrinks = new Correlator(slotQueryColdDrinks, slotOutputReplicatorColdDrinksToCorrelator, slotOutput1CorrelatorColdDrinks, slotOutput2CorrelatorColdDrinks);
        ContextEnricher teColdDrinks = new ContextEnricher(slotOutput1CorrelatorColdDrinks, slotOutput2CorrelatorColdDrinks, slotOutputEnricherColdDrinks);

        //TAREAS COMUNES FINALES:
        Merger tm = new Merger(slotOutputEnricherHotDrinks, slotOutputEnricherColdDrinks, slotMergerToAgregator);
        Aggregator ta = new Aggregator(slotMergerToAgregator, slotOutputAggregator);

        try {
            System.out.println("Inserte fichero para leer: ");
            Scanner sc = new Scanner(System.in);
            String nombrefichero = sc.nextLine();
            String fichero = "comandas/" + nombrefichero;
            String order_id = ic.run(fichero);
            ip.writeSlotInput(ic.getDocument()); //escribe el documento en el slot inicial

            //--------------- TAREAS COMUNES ENTRE BEBIDAS FRIAS Y CALIENTES ------------
            //TAREA SPLITTER
            System.out.println("******************************************");
            System.out.println("Corriendo Splitter...");
            ts.run();
            //TAREA DISTRIBUTOR
            System.out.println("Corriendo Distributor...");
            System.out.println("******************************************");
            td.run();

            //---------------------------- BEBIDAS CALIENTES ----------------------
            //REPLICATOR
            System.out.println("Corriendo Replicator para bebidas calientes...");
            trHotDrinks.run();
            trHotDrinks.generaXML(slotOutputReplicatorHotDrinksToTranslator, slotOutputReplicatorHotDrinksToCorrelator);//generates the xml needed for the translator
            //TRANSLATOR
            System.out.println("Corriendo Translator para bebidas calientes...");
            ttHotDrinks.run();
            //CONSULTA STOCK BEBIDAS CALIENTES
            System.out.println("Barman bebidas calientes consultando stock...");
            rPortHotDrinks.doWork();
            //CORRELATOR
            System.out.println("Corriendo Correlator para bebidas calientes...");
            tcHotDrinks.run();
            //CONTENT ENRICHER
            System.out.println("Corriendo Context Enricher para bebidas calientes...");
            teHotDrinks.run();

            //---------------------------- BEBIDAS FRIAS --------------------------
            //REPLICATOR
            System.out.println("******************************************");
            System.out.println("Corriendo Replicator para bebidas frias...");
            trColdDrinks.run();
            trColdDrinks.generaXML(slotOutputReplicatorColdDrinksToTranslator, slotOutputReplicatorColdDrinksToCorrelator); //generates the xml needed for the translator
            //TRANSLATOR
            System.out.println("Corriendo Translator para bebidas frias...");
            ttColdDrinks.run();
            //CONSULTA STOCK BEBIDAS FRIAS
            System.out.println("Barman bebidas frias consultando stock...");
            rPortColdDrinks.doWork();
            //CORRELATOR
            System.out.println("Corriendo Correlator para bebidas frias...");
            tcColdDrinks.run();
            //CONTEXT ENRICHER
            System.out.println("Corriendo Context Enricher para bebidas frias...");
            teColdDrinks.run();

            //--------------------------- OTRAS TAREAS COMUNES --------------------
            //MERGER
            System.out.println("******************************************");
            System.out.println("Corriendo Merger...");
            tm.run();
            //AGGREGATOR:
            System.out.println("Corriendo Aggregator...");
            ta.run("1");

            //OUTPUT CONNECTOR
            System.out.println("******************************************");
            String f = "salida_" + nombrefichero;
            System.out.println("Guardando la comanda en el fichero:" + f);
            String ficherosalida = "comandas/" + f;
            OutputConnector cs = new OutputConnector(slotOutputAggregator.read());
            cs.generate(ficherosalida);

        } catch (TransformerException ex) {
        }
    }

}
