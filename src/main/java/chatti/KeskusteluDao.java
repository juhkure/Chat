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
    
    public List<Keskustelu> findAvaukset() throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Keskustelu;");
        
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

    public void lisaaKeskustelu(int id, int alue, String avaus) throws SQLException {
        Connection connection = database.getConnection();
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Keskustelu (id, alue, avaus) VALUES\n"
                + "('" + id + "', '" + alue + "', '" + avaus + "');");

        stmt.executeUpdate();
        stmt.close();
        connection.close();
    }

}
