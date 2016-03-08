package chatti;

import java.util.*;
import java.sql.*;

public class ViestiDao {

    private chatti.Database database;

    public ViestiDao(chatti.Database database) {
        this.database = database;
    }

    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti;");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int keskustelu = rs.getInt("keskustelu");
            int lahettaja = rs.getInt("lähettäjä");
            String sisalto = rs.getString("sisältö");
            String aika = rs.getString("aika");

            viestit.add(new Viesti(id, keskustelu, lahettaja, sisalto, aika));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    public List<Viesti> find(int keskustelu) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti "
                + "WHERE keskustelu = " + keskustelu + ";");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int lahettaja = rs.getInt("lähettäjä");
            String sisalto = rs.getString("sisältö");
            String aika = rs.getString("aika");

            viestit.add(new Viesti(id, keskustelu, lahettaja, sisalto, aika));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    public void lisaaViesti(int keskustelu, String sisalto, String lahettaja) throws SQLException {
        Connection connection = database.getConnection();
        
        // Tsekataan löytyykö lähettäjä jo valmiiksi tietokannasta
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Lähettäjä WHERE nimi = '" + lahettaja + "';");
        ResultSet rs = stmt.executeQuery();
        int lahettajaId = 0;
        while (rs.next()) {
            lahettajaId = rs.getInt("id");
        }

        // Jos lähettäjää ei löytynyt, lisätään se ja haetaan id.
        if (lahettajaId == 0) {
            stmt = connection.prepareStatement("INSERT INTO Lähettäjä (nimi) VALUES ('" + lahettaja + "');");
            stmt.executeUpdate();
            stmt = connection.prepareStatement("SELECT * FROM Lähettäjä WHERE nimi = '" + lahettaja + "';");
            rs = stmt.executeQuery();
            while (rs.next()) {
                lahettajaId = rs.getInt("id");
            }
        }

        stmt = connection.prepareStatement("INSERT INTO Viesti (keskustelu, lähettäjä, sisältö) VALUES\n"
                + "('" + keskustelu + "', '" + lahettajaId + "', '" + sisalto + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

}
