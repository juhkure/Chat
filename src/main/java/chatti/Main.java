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
            for (Viesti viesti : viestit) {
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
           /*
             return "Viestisi on nyt lisätty keskusteluihin.<br/>"
             + "<a href='https://enigmatic-badlands-83870.herokuapp.com/viestit?keskustelu=" + keskustelu + "'>Palaa viesteihin.</a>";
             */
            return "Viestisi on nyt lisätty keskusteluihin.<br/>"
                    + "<a href='/viestit?keskustelu=" + keskustelu + "'>Palaa viesteihin.</a>";

        });

        // **********************
        // KESKUSTELUT - GET ****
        get("/keskustelut", (req, res) -> {
            int alue_id = Integer.parseInt(req.queryParams("alue"));
            String alue = alueDao.findAlue(alue_id);
            int lkm = keskusteluDao.viestienLkmKeskustelualueessa(alue_id);

            String html = "<h1>Vauva-chat</h1>\n";
            html += "<h2>Keskustelualue: " + alue + "</h2>\n";
            html += "<h3>Viestejä yhteensä " + lkm + " kpl</h3>";

            List<Keskustelu> avaukset = keskusteluDao.findAvaukset(alue_id);

            html += "<table border='1'><th>Viimeisin Viesti</th><th>Viestejä yhteensä</th><th>Keskustelun avaus</th>";

            for (Keskustelu keskustelu : avaukset) {
                String aika = keskusteluDao.viimeisimmanViestinAika(keskustelu.getId());
                int viestit = viestiDao.viestienLkmKeskustelussa(keskustelu.getId());
                html += "<tr>";
                html += "<td>" + aika + "</td>";
                html += "<td>" + viestit + "</td>";
                html += "<td><a href='/viestit?keskustelu=" + keskustelu.getId() + "'>" + keskustelu.getAvaus() + "</a></td>\n";
                html += "</tr>";
            }
            html += "</table>";
            
            

            //html += "<h4><a href='https://enigmatic-badlands-83870.herokuapp.com/alueet'>Palaa keskusteluaiheiden valintaan</a>";
            html += "<h3><a href='/alueet'>Palaa keskusteluaiheiden valintaan</a></h3>";
            
            html += "<h3>Tee uusi keskusteluavaus</h3>";
            
            html += "<form name='lomake' method='POST' action='/keskustelut'>";
            html += "Aihe: <input type='text' name='avaus' required><br/>";
            html += "Viesti: <input type='text' name='sisalto' required><br/>";
            html += "Lähettäjä: <input type='text' name='lahettaja' required><br/>";
            html += "<input type='hidden' name='alue' value='" + alue_id + "'><br/>";
            html += "<input type='submit' value='Lähetä keskusteluavaus' onclick='tarkista();'>";
            html += "</form>";
            
            html += tarkistuskoodi();

            return html;
        });

        // *******************
        // KESKUSTELUT - POST ****
        post("/keskustelut", (req, res) -> {
            String avaus = req.queryParams("avaus");
            String sisalto = req.queryParams("sisalto");
            String lahettaja = req.queryParams("lahettaja");
            int alue_id = Integer.parseInt(req.queryParams("alue"));

            keskusteluDao.lisaaKeskustelu(alue_id, avaus);
            
            int keskusteluId = keskusteluDao.viimeisimmanViestinId();
            
            viestiDao.lisaaViesti(keskusteluId, sisalto, lahettaja);

            //Tämä return-versio liittyy Heroku-palvelimeen. Vähän halpa ratkaisu..
           /*
             return "Viestisi on nyt lisätty keskusteluihin.<br/>"
             + "<a href='https://enigmatic-badlands-83870.herokuapp.com/keskustelut?alue=" + alue_id + "'>Palaa keskusteluiihin.</a>";
             */
            String html = "Keskusteluavauksesi on nyt lisätty.<br/>"
                    + "<a href='/keskustelut?alue=" + alue_id + "'>Palaa keskusteluihin.</a>";
                    
            
                    
            return html;
        });
        

        
        //*****************
        //ALUEET - GET ****
        get("/alueet", (req, res) -> {

            String html = "<h1>Tervetuloa keskustelemaan!</h1>\n";
            html += "<h1>Keskustelualueet:</h1>";

            List<Alue> alueet = alueDao.findAll();

            html += "<table border='1'><th>Alue</th><th>Viestejä yhteensä</th><th>Viimeisin viesti</th>";

            for (Alue alue : alueet) {

                String pvm = alueDao.viimeisimmanViestinPvm(alue.getId());
                int lkm = alueDao.viestienLkm(alue.getId());

                html += "<tr>";
                html += "<td><a href='/keskustelut?alue=" + alue.getId() + "'>" + alue.getNimi() + "</a></td>\n";
                html += "<td align=\"center\">" + lkm + "</td> ";
                html += "<td align=\"center\">" + pvm + "</td>";

                html += "</tr>";
            }
            html += "</table>";
            html += "<form method=\"POST\" action=\"/alueet\"> <br/><b>Lisää uusi alue:<b/><br/>";
            html += "Alue: <input type=\"text\" name=\"alue\" required><br/>";
            html += "<input type=\"submit\" value=\"Lisää\"/>";
            html += "</form>";

            //html += "<h4><a href='https://enigmatic-badlands-83870.herokuapp.com/alueet'>Palaa keskusteluaiheiden valintaan</a>";
//            html += "<h3><a href='/alueet'>Palaa alueiden valintaan</a></h3>";
            return html;
        });
                
        // *******************
        // ALUEET - POST ****
        post("/alueet", (req, res) -> {
            String nimi = req.queryParams("alue");
            alueDao.lisaaAlue(nimi);
           
            return "Uusi alue on nyt lisätty chattiin.<br/>"
                    + "<h3><a href=/alueet >Takaisin etusivulle</a></h3>";
           
        });
    }
    
    public static String tarkistuskoodi() {
        String koodi = "<script>"
                + "function tarkista() {"
                + " var x = document.forms['lomake']['avaus'].value;"
                + " if(x == null || x == '') {"
                + "     alert('Anna keskusteluaihe!');"
                + "}"
                + " x = document.forms['lomake']['sisalto'].value;"
                + " if(x == null || x == '') {"
                + "     alert('Kirjoita avaukseen liittyvä viesti!');"
                + "}"
                + " x = document.forms['lomake']['lahettaja'].value;"
                + " if(x == null || x == '') {"
                + "     alert('Lähettäjä puuttuu!');"
                + "}"
                + "    }  </script>";
        
        return koodi;
    }
}
