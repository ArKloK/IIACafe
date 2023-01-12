package Connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ConnectorDB {

    private static Connection conn;
    String dbClass = "com.mysql.cj.jdbc.Driver";
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private DocumentBuilder builder;
    private DOMImplementation implementation;
    private Document document;
    private Element root;

    //Connects to the database and test the connection
    public void conexion() throws InstantiationException, IllegalAccessException, SQLException {
        System.out.println("Connecting ...");

        try {
            Class.forName(dbClass).newInstance();
            System.out.println("Driver loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to load driver " + e);
        }

        try {
            ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");
            System.out.println("Connection established.");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e);
        }

        conn.close(); // ¿ConnectorDB.conn.close()? --> static 
        System.out.println("Connection closed.");
    }

    //Remove an unit from stock
    void removeStock(String kindDrink, String drink) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int stock = -1;

        System.out.print("Remove");
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
        System.out.println("\nRemoved");
        conn.close();
    }

    //Check the amount of stock of a drink
    public int findOutStock(String kindDrink, String drink) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int stock = 0;

        //System.out.println("Find out");
        Class.forName(dbClass);

        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        try {
            Statement stmt = conn.createStatement();

            try {
                ResultSet rs = stmt.executeQuery("SELECT stock FROM " + kindD + " WHERE nombre= '" + drk + "'");

                //System.out.println("Readed");
                try {
                    while (rs.next()) {
                        int numColumns = rs.getMetaData().getColumnCount();
                        for (int i = 1; i <= numColumns; i++) {
                            //Column numbers start at 1.
                            //Also there are many methods on the result set to return the column as a particular type. Refer to the sun documentation for the list of valid conversions
                            //System.out.println("COLUMN" + i + " = " + rs.getObject(i));
                            stock = rs.getInt("stock");
                        }
                    }
                } finally {
                    try {
                        rs.close();
                    } catch (SQLException ignore) {
                        //Propagate the original exception instead of this one that you may want just logged
                    }
                }
            } finally {
                try {
                    stmt.close();
                } catch (SQLException ignore) {
                    //Propagate the original exception instead of this one that you may want just logged
                }
            }
        } finally {
            conn.close();
            /*try
            {
                //It's important to close the connection when you are done with it
                conn.close();
            }catch(SQLException ignore)
            {
                //Propagate the original exception instead of this one that you may want just logged
            }*/
        }

        return stock;
    }

    public Document lookCommand(Document command) throws ClassNotFoundException, SQLException, ParserConfigurationException {
        String drink;
        NodeList nList = command.getElementsByTagName("name");

        for (int i = 0; i < nList.getLength(); i++) {
            drink = nList.item(i).getTextContent();
            String stock;
            int number_stock = findOutStock(kindDrink(drink), drink);
            System.out.println(number_stock);

            if (number_stock > 0) {
                stock = String.valueOf(number_stock);

                Element root2 = command.getDocumentElement(); //root node
                Element keyNode = command.createElement("stock");

                Text nodeKeyValue = command.createTextNode("true");

                keyNode.appendChild(nodeKeyValue);
                root2.appendChild(keyNode);
                removeStock(kindDrink(drink), drink);
            } else {
                stock = "0";

                Element root2 = command.getDocumentElement(); //root node
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
