package chatti;

import java.sql.*;
import java.util.*;
import java.net.*;

public class Database {

    private String databaseAddress;

    public Database(String databaseAddress) throws Exception {
        this.databaseAddress = databaseAddress;

        init();
    }

    private void init() {
        List<String> lauseet = null;
        if (this.databaseAddress.contains("postgres")) {
            lauseet = postgreLauseet();
        } else {
            lauseet = sqliteLauseet();
        }

        // "try with resources" sulkee resurssin automaattisesti lopuksi
        try (Connection conn = getConnection()) {
            Statement st = conn.createStatement();

            // suoritetaan komennot
            for (String lause : lauseet) {
                System.out.println("Running command >> " + lause);
                st.executeUpdate(lause);
            }

        } catch (Throwable t) {
            // jos tietokantataulu on jo olemassa, ei komentoja suoriteta
            System.out.println("Error >> " + t.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.databaseAddress.contains("postgres")) {
            try {
                URI dbUri = new URI(databaseAddress);

                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Throwable t) {
                System.out.println("Error: " + t.getMessage());
                t.printStackTrace();
            }
        }

        return DriverManager.getConnection(databaseAddress);
    }

    private List<String> postgreLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("DROP TABLE Alue;");
        // heroku käyttää SERIAL-avainsanaa uuden tunnuksen automaattiseen luomiseen
        lista.add("CREATE TABLE Viesti (id SERIAL PRIMARY KEY, sisalto varchar(140), aika timestamp, kayttaja varchar(255));");
        lista.add("INSERT INTO Viesti (sisalto, kayttaja) VALUES\n"
                + "('hello world', ‘ope’),\n"
                + "('hei maailma', 'pertti'),\n"
                + "('hei pertti', 'tiina'),\n"
                + "('haistakaa tikkari', 'julle'),\n"
                + "('nohnoh', 'ope');");

        return lista;
    }

    private List<String> sqliteLauseet() {
        ArrayList<String> lista = new ArrayList<>();

        // tietokantataulujen luomiseen tarvittavat komennot suoritusjärjestyksessä
        lista.add("CREATE TABLE Viesti (id integer primary key, sisalto varchar(140), aika timestamp, kayttaja varchar(255));");
        lista.add("INSERT INTO Viesti (sisalto, kayttaja) VALUES\n"
                + "('hello world', ‘ope’),\n"
                + "('hei maailma', 'pertti'),\n"
                + "('hei pertti', 'tiina'),\n"
                + "('haistakaa tikkari', 'julle'),\n"
                + "('nohnoh', 'ope');");

        return lista;
    }
}
