
package chatti;

import java.sql.*;


public class Viesti {
    
    private int id;
    private String sisalto;
    private Timestamp aika;
    private String kayttaja;

    public Viesti(int id, String sisalto, Timestamp aika, String kayttaja) {
        this.id = id;
        this.sisalto = sisalto;
        this.aika = aika;
        this.kayttaja = kayttaja;
    }

    public int getId() {
        return id;
    }

    public String getKayttaja() {
        return kayttaja;
    }

    public Timestamp getAika() {
        return aika;
    }

    public String getSisalto() {
        return sisalto;
    }
    
    
    
}
