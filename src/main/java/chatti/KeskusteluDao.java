package chatti;

import java.sql.*;
import java.util.*;

public class KeskusteluDao {

    private chatti.Database database;

    public KeskusteluDao(chatti.Database database) {
        this.database = database;
    }

    public List<Keskustelu> findAll() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu;");

        ResultSet rs = stmt.executeQuery();
        List<Keskustelu> keskustelut = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            int alue = rs.getInt("alue");
            String avaus = rs.getString("avaus");

            keskustelut.add(new Keskustelu(id, alue, avaus));
        }

        rs.close();
        stmt.close();
        connection.close();

        return keskustelut;
    }

    public String findAvaus(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu "
                + "WHERE id = " + id + ";");

        String avaus = "";
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            avaus = rs.getString("avaus");
        }

        return avaus;
    }

    public List<Keskustelu> findAvaukset(int alue_id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu WHERE alue_id = " + alue_id + ";");

        List<Keskustelu> avaukset = new ArrayList<Keskustelu>();
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String avaus = rs.getString("avaus");
            int id = rs.getInt("id");
            int alue = rs.getInt("alue_id");
            avaukset.add(new Keskustelu(id, alue, avaus));
        }

        return avaukset;
    }

    public void lisaaKeskustelu(int alue, String avaus) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (alue, avaus) VALUES\n"
                + "('" + alue + "', '" + avaus + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

    public int viestienLkm(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(Viesti.id) FROM Viesti, Keskustelu WHERE Viesti.keskustelu=keskustelu.id AND Keskustelu.id = '" + id
                + "';");

        ResultSet rs = stmt.executeQuery();
        stmt.executeUpdate();
        stmt.close();
        connection.close();

        String vastaus = "" + rs;
        int lkm = Integer.parseInt(vastaus);

        return lkm;
    }

    public String viimeisimmanViestinPvm(int id) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT Viesti.aika FROM Viesti JOIN Keskustelu\n"
                + "ON Viesti.keskustelu = Keskustelu.id\n"
                + "WHERE Keskustelu.id = '" + id + "' ORDER BY Viesti.aika DESC LIMIT 1;");
        String pvm = stmt.toString();

        return pvm.substring(0, 9);
    }
    
    public int findAlue(int keskustelu)  throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT alue_id FROM Keskustelu "
                + "WHERE id = " + keskustelu + ";");
        
        ResultSet rs = stmt.executeQuery();
        int alue_id = 0;
        while(rs.next()) {
            alue_id = rs.getInt("alue_id");
        }
        
        rs.close();
        stmt.close();
        connection.close();
        
        return alue_id;
    }

}
