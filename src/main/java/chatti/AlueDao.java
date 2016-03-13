
package chatti;

import java.sql.*;
import java.util.*;

public class AlueDao {

    private Database database;

    public AlueDao(Database database) {
        this.database = database;
    }

    public List<Alue> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue;");

        ResultSet rs = stmt.executeQuery();
        List<Alue> alueet = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            String nimi = rs.getString("nimi");

            alueet.add(new Alue(id, nimi));
        }

        rs.close();
        stmt.close();
        connection.close();

        return alueet;
    }

    public String findAlue(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Alue "
                + "WHERE id = " + id + ";");

        String alue = "";
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            alue = rs.getString("alue");
        }

        return alue;
    }

    public void lisaaAlue(int id, String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES\n"
                + "('" + nimi + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }
    

}