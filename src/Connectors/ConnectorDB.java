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

        //conn.close(); // ¿ConnectorDB.conn.close()? --> static 
        System.out.println("Connection closed.");
    }

    //Check the stock of a certain drink kind
    void checkStock(String kindDrink, String drink) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int stock;
        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        System.out.println("Check");
        Class.forName(dbClass);

        try {
            Statement stmt = conn.createStatement();

            try {
                ResultSet rs = stmt.executeQuery("SELECT stock FROM " + kindD + " WHERE nombre='" + drk + "'");
                System.out.println("Readed");

                try {
                    while (rs.next()) {
                        int numColumns = rs.getMetaData().getColumnCount();
                        for (int i = 1; i <= numColumns; i++) {
                            //Column numbers start at 1.
                            //Also there are many methods on the result set to return the column as a particular type. Refer to the sun documentation for the list of valid conversions.
                            System.out.println("COLUMN " + i + " = " + rs.getObject(i));
                            stock = rs.getInt("stock");
                            System.out.println(stock);
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
            //It's important to close the connection when you are done with it
            try {
                conn.close();
            } catch (SQLException ignore) {
                //Propagate the original exception instead of this one that you may want just logged
            }
        }
    }

    //Remove an unit from stock
    void removeStock(String kindDrink, String drink, int stock) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int sto = stock;
        sto = sto - 1;

        System.out.print("Remove");
        Class.forName(dbClass);

        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        PreparedStatement stmt;
        stmt = conn.prepareStatement("UPDATE " + kindD + "SET stock=" + sto + " WHERE nombre='" + drk + "'");
        int ret = stmt.executeUpdate();
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

    //Increase the stock
    void increaseStock(String kindDrink, String drink, int stock, int increase) throws ClassNotFoundException, SQLException {
        String kindD = kindDrink;
        String drk = drink;
        int sto = stock;
        int inc = increase;
        sto = sto + increase;

        //System.out.println("Increase");
        Class.forName(dbClass);

        ConnectorDB.conn = DriverManager.getConnection("jdbc:mysql://sql7.freemysqlhosting.net:3306/sql7589305", "sql7589305", "UcN2ZDysbJ");

        PreparedStatement stmt;
        stmt = conn.prepareStatement("UPDATE " + kindD + " SET stock=" + sto + " WHERE nombre='" + drk + "'");
        int ret = stmt.executeUpdate();
        //System.out.println("Increased");
        conn.close();
    }

    public Document lookCommand(Document command) throws ClassNotFoundException, SQLException, ParserConfigurationException {
        String drink;
        NodeList nList = command.getElementsByTagName("nombre");

        for (int i = 0; i < nList.getLength(); i++) {
            drink = nList.item(i).getTextContent();
            String stock;
            int number_stock = findOutStock(kindDrink(drink), drink);

            if (number_stock > 0) {
                stock = String.valueOf(number_stock);

                Element root2 = command.getDocumentElement(); //root node
                Element keyNode = command.createElement("stock");

                Text nodeKeyValue = command.createTextNode("true");

                keyNode.appendChild(nodeKeyValue);
                root2.appendChild(keyNode);
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
        if (("cafe".equals(drink)) | ("te".equals(drink))) {
            return "BebidasCalientes";
        } else {
            return "BebidasFrias";
        }
    }
}
