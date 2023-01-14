package Connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ConnectorDB {

    private static Connection conn;
    private final String dbClass = "com.mysql.cj.jdbc.Driver";

    //Comprueba la conexion a la base de datos
    public void conexion() throws InstantiationException, IllegalAccessException, SQLException {
        System.out.println("Conectando ...");

        try {
            Class.forName(dbClass).newInstance();
            System.out.println("Driver cargado");
        } catch (ClassNotFoundException e) {
            System.out.println("No se pudo cargar el driver " + e);
        }

        try {
            ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");
            System.out.println("Conexion exitosa.");
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e);
        }

        conn.close(); // ¿ConnectorDB.conn.close()? --> static 
        System.out.println("Conexion cerrada.");
    }

    //Elimina una unidad del stock de la bebida drink en su correspondiente tabla
    void removeStock(String kindDrink, String drink) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int stock = -1;

        System.out.print("Eliminando stock de " + drink);
        Class.forName(dbClass);

        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        Statement st;
        PreparedStatement stmt;

        st = conn.createStatement();
        ResultSet rs = st.executeQuery("SELECT stock FROM " + kindD + " WHERE nombre= '" + drk + "'");
        if (rs.next()) {
            stock = rs.getInt("stock");
            stock--;
        }
        rs.close();

        stmt = conn.prepareStatement("UPDATE " + kindD + " SET stock= " + stock + " WHERE nombre='" + drk + "'");
        stmt.executeUpdate();
        stmt.close();
        System.out.println("\nStock de " + drink + " eliminado");
        conn.close();
    }

    //Comprueba la cantidad de stock de una bebida
    public int findOutStock(String kindDrink, String drink) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int stock = 0;
        
        Class.forName(dbClass);

        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        try {
            Statement stmt = conn.createStatement();

            try {
                ResultSet rs = stmt.executeQuery("SELECT stock FROM " + kindD + " WHERE nombre= '" + drk + "'");
                
                try {
                    while (rs.next()) {
                        int numColumns = rs.getMetaData().getColumnCount();
                        for (int i = 1; i <= numColumns; i++) {
                            stock = rs.getInt("stock");
                        }
                    }
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ignore) {}
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException ignore) {}
            }
        } finally {
            //Cerramos conexion aun si ha habido fallos
            conn.close();
        }

        return stock;
    }

    public Document lookCommand(Document command) throws ClassNotFoundException, SQLException, ParserConfigurationException {
        String drink;
        NodeList nList = command.getElementsByTagName("name");

        for (int i = 0; i < nList.getLength(); i++) {
            drink = nList.item(i).getTextContent();
            int number_stock = findOutStock(kindDrink(drink), drink);

            if (number_stock > 0) {

                Element root2 = command.getDocumentElement(); //nodo raiz
                Element keyNode = command.createElement("stock");

                Text nodeKeyValue = command.createTextNode("true");

                keyNode.appendChild(nodeKeyValue);
                root2.appendChild(keyNode);
                removeStock(kindDrink(drink), drink);
            } else {

                Element root2 = command.getDocumentElement(); //nodo raiz
                Element keyNode = command.createElement("stock");

                Text nodeKeyValue = command.createTextNode("false");

                keyNode.appendChild(nodeKeyValue);
                root2.appendChild(keyNode);
            }
        }

        return command;
    }

    String kindDrink(String drink) {
        if (("cafe".equals(drink)) | ("te".equals(drink)) | ("chocolate".equals(drink)) | ("tila".equals(drink))) {
            return "BebidasCalientes";
        } else {
            return "BebidasFrias";
        }
    }
}
