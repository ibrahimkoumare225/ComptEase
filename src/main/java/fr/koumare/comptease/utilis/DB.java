package fr.koumare.comptease.utilis;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    Connection conn = null;
    public static Connection conDB()
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost/datab", "jouney","ilaydahafiza");
            return con;
        } catch (ClassNotFoundException | SQLException ex) {
            System.err.println("ConnectionUtil : "+ex.getMessage());
            return null;
        }
    }
}
