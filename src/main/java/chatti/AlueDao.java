
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
        PreparedStatement stmt = connection.prepareStatement("SELECT nimi FROM Alue "
                + "WHERE id = " + id + ";");

        String alue = "";
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            alue = rs.getString("nimi");
        }

        rs.close();
        stmt.close();
        connection.close();
        
        return alue;
    }

    public void lisaaAlue(String nimi) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Alue (nimi) VALUES ('" + nimi + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }
    
    public int viestienLkm(int alueid) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(Viesti.id) AS lkm FROM Viesti\n"
                + "INNER JOIN Keskustelu ON Viesti.keskustelu = keskustelu.id "
                + "INNER JOIN Alue ON Keskustelu.alue_id = Alue.id "
                + "WHERE Alue.id = '" + alueid +"';");

        ResultSet rs = stmt.executeQuery();

        int lkm = 0;
        while (rs.next()) {
            lkm = rs.getInt("lkm");
        }

        rs.close();
        stmt.close();
        connection.close();
        
        return lkm;
    }
    
    public String viimeisimmanViestinPvm(int alueid) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Viesti.aika AS aika FROM Viesti JOIN Keskustelu\n"
                + "ON Viesti.keskustelu = Keskustelu.id\n"
                + "WHERE Keskustelu.id = '" + alueid + "' ORDER BY Viesti.aika DESC LIMIT 1;");
        ResultSet rs = stmt.executeQuery();
        
        String aika = "";
        while (rs.next()) {
            aika = rs.getString("aika");
        }

        rs.close();
        stmt.close();
        connection.close();
        return aika.substring(0, 10);
    }
    


}