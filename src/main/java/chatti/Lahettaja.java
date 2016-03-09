
package chatti;

public class Lahettaja {
    private int id;
    private String nimi;

    public Lahettaja(int id, String nimi) {
        this.id = id;
        this.nimi = nimi;
    }

    public int getId() {
        return id;
    }

    public String getNimi() {
        return nimi;
    }
    
}
