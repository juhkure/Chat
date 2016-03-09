
package chatti;

import java.sql.*;
import java.util.*;

public class LahettajaDao {
    private Database database;

    public LahettajaDao(Database database) {
        this.database = database;
    }

    public List<Lahettaja> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Lahettaja;");

        ResultSet rs = stmt.executeQuery();
        List<Lahettaja> lahettajat = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            lahettajat.add(new Lahettaja(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return lahettajat;
    }

    public void lisaaLahettaja(int id, String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Lahettaja (id, nimi) VALUES\n"
                + "('" + id + "', '" + nimi + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }
    
    public String haeNimimerkki(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT nimi FROM lähettäjä \n"
                + "WHERE id = '" + id + "';");

        ResultSet rs = stmt.executeQuery();
        
        String nimi = "";
        while (rs.next()) {
            nimi = rs.getString("nimi");
        }
        
        
        rs.close();
        stmt.close();
        connection.close();
        
        return nimi;
    }

}