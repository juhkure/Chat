
package chatti;

import java.sql.*;
import java.util.*;
import spark.ModelAndView;
import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

public class Main {
    public static void main(String[] args) throws Exception {

        
        // asetetaan portti jos heroku antaa PORT-ympäristömuuttujan
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }
        
        // käytetään oletuksena paikallista sqlite-tietokantaa
        String jdbcOsoite = "jdbc:sqlite:chat.db";
        // jos heroku antaa käyttöömme tietokantaosoitteen, otetaan se käyttöön
        if (System.getenv("DATABASE_URL") != null) {
            jdbcOsoite = System.getenv("DATABASE_URL");
        } 

        Database db = new Database(jdbcOsoite);
        
        ViestiDao viestiDao = new ViestiDao(db);
        
        get("/viestit", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("viestit", viestiDao.findAll());

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());
        
        post("/viestit", (req, res) -> {
            String sisalto = req.queryParams("sisalto");
            String kayttaja = req.queryParams("kayttaja");
            /*
            java.util.Date date= new java.util.Date();
            Timestamp aika = new Timestamp(date.getTime());
            */
            viestiDao.lisaaViesti(sisalto, kayttaja);
                    
            return "Viestisi on nyt lisätty päivän keskusteluihin.<br/>"
                    + "<a href='/viestit'>Palaa keskusteluun.</a>";
        });
        
    }
}
