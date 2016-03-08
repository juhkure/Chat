
package chatti;

public class Viesti {
    private int id;
    private int keskustelu;
    private int lahettaja;
    private String sisalto;
    private String aika;

    public Viesti(int id, int keskustelu, int lahettaja, String sisalto, String aika) {
        this.id = id;
        this.keskustelu = keskustelu;
        this.lahettaja = lahettaja;
        this.sisalto = sisalto;
        this.aika = aika;
    }

    public int getId() {
        return id;
    }

    public int getKeskustelu() {
        return keskustelu;
    }

    public int getLahettaja() {
        return lahettaja;
    }

    public String getSisalto() {
        return sisalto;
    }

    public String getAika() {
        return aika;
    }
}