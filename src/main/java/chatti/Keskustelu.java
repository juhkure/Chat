
package chatti;


public class Keskustelu {
    private int id;
    private int alue;
    private String avaus;

    public Keskustelu(int id, int alue, String avaus) {
        this.id = id;
        this.alue = alue;
        this.avaus = avaus;
    }

    public int getId() {
        return id;
    }

    public int getAlue() {
        return alue;
    }

    public String getAvaus() {
        return avaus;
    } 
}