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
        KeskusteluDao keskusteluDao = new KeskusteluDao(db);

        get("/viestit", (req, res) -> {
            int keskusteluId = Integer.parseInt(req.queryParams("keskustelu"));
            
            HashMap map = new HashMap<>();
            map.put("keskustelu", keskusteluDao.findAvaus(keskusteluId));
            map.put("viestit", viestiDao.find(keskusteluId));
            map.put("keskusteluId", keskusteluId);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        post("/viestit", (req, res) -> {
            String sisalto = req.queryParams("sisalto");
            String lahettaja = req.queryParams("lahettaja");
            int keskustelu = Integer.parseInt(req.queryParams("keskustelu"));
            
            viestiDao.lisaaViesti(keskustelu, sisalto, lahettaja);

            return "Viestisi on nyt lisätty päivän keskusteluihin.<br/>"
                    + "<a href='/viestit?keskustelu=" + keskustelu + "'>Palaa viesteihin.</a>";
        });
        
        
        get("/keskustelut", (req, res) -> {
            String html = "<h1>Tervetuloa chattiin!</h1>\n";
            html += "<h2>Keskustelualueet ovat:</h2>\n";
            
            List<Keskustelu> avaukset = keskusteluDao.findAvaukset();
            
            for(Keskustelu keskustelu : avaukset) {
                html += "<a href='/viestit?keskustelu="+keskustelu.getId()+"'>"+keskustelu.getAvaus()+"</a><br>\n";
            }
            
            
            return html;
        });
    }
}