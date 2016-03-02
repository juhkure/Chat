package chatti;

import java.util.*;
import java.sql.*;

public class ViestiDao {

    private Database database;

    public ViestiDao(Database database) {
        this.database = database;
    }

    public List<Viesti> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Viesti;");

        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String sisalto = rs.getString("sisalto");
            Timestamp aika = rs.getTimestamp("aika");
            String kayttaja = rs.getString("kayttaja");

            viestit.add(new Viesti(id, sisalto, aika, kayttaja));
        }

        rs.close();
        stmt.close();
        connection.close();

        return viestit;
    }

    public void lisaaViesti(String sisalto, String kayttaja) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Viesti (sisalto, kayttaja) VALUES\n"
                + "('" + sisalto + "', '" + kayttaja + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

}
