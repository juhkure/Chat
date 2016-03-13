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
        LahettajaDao lahettajaDao = new LahettajaDao(db);
        AlueDao alueDao = new AlueDao(db);

        // ******************
        // VIESTIT - GET ****
        get("/viestit", (req, res) -> {
            int keskusteluId = Integer.parseInt(req.queryParams("keskustelu"));
            int alue_id = keskusteluDao.findAlue(keskusteluId);
            
            HashMap map = new HashMap<>();
            map.put("keskustelu", keskusteluDao.findAvaus(keskusteluId));
            List<Viesti> viestit = viestiDao.find(keskusteluId);
            for(Viesti viesti : viestit) {
                viesti.setNimimerkki(lahettajaDao.haeNimimerkki(viesti.getLahettaja()));
            }
            map.put("viestit", viestit);
            map.put("keskusteluId", keskusteluId);
            map.put("alue_id", alue_id);

            return new ModelAndView(map, "index");
        }, new ThymeleafTemplateEngine());

        // *******************
        // VIESTIT - POST ****
        post("/viestit", (req, res) -> {
            String sisalto = req.queryParams("sisalto");
            String lahettaja = req.queryParams("lahettaja");
            int keskustelu = Integer.parseInt(req.queryParams("keskustelu"));
            
            viestiDao.lisaaViesti(keskustelu, sisalto, lahettaja);
            
            //Tämä return-versio liittyy Heroku-palvelimeen. Vähän halpa ratkaisu..
            
            return "Viestisi on nyt lisätty keskusteluihin.<br/>"
                    + "<a href='https://enigmatic-badlands-83870.herokuapp.com/viestit?keskustelu=" + keskustelu + "'>Palaa viesteihin.</a>";
            
            /*
            return "Viestisi on nyt lisätty keskusteluihin.<br/>"
                    + "<a href='/viestit?keskustelu=" + keskustelu + "'>Palaa viesteihin.</a>";
                    */
        });
        
        // **********************
        // KESKUSTELUT - GET ****
        get("/keskustelut", (req, res) -> {
            int alue_id = Integer.parseInt(req.queryParams("alue"));
            
            String html = "<h1>Tervetuloa chattiin!</h1>\n";
            html += "<h2>Keskustelujen aiheet ovat:</h2>\n";
            
            List<Keskustelu> avaukset = keskusteluDao.findAvaukset(alue_id);
            
            for(Keskustelu keskustelu : avaukset) {
                html += "<a href='/viestit?keskustelu="+keskustelu.getId()+"'>"+keskustelu.getAvaus()+"</a><br>\n";
            }
            
            html += "<h4><a href='https://enigmatic-badlands-83870.herokuapp.com/alueet'>Palaa keskusteluaiheiden valintaan</a>";
            //html += "<h4><a href='/alueet'>Palaa keskusteluaiheiden valintaan</a>";
            
            
            return html;
        });
        
        //*****************
        //ALUEET - GET ****
        get("/alueet", (rq, res) -> {
            String html = "<h1>Tervetuloa chattiin!</h1>\n";
            html += "<h2>Keskustelujen aiheet ovat:</h2>\n";
            
            List<Alue> alueet = alueDao.findAll();
            
            for (Alue alue : alueet) {
                html += "<a href='/keskustelut?alue="+alue.getId()+"'>"+alue.getNimi()+"</a><br>\n";
            }
            
            return html;
        });
    }
}