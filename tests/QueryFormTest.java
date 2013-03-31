import nl.ctammes.common.MijnIni;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 4-3-13
 * Time: 7:03
 * To change this template use File | Settings | File Templates.
 */
public class QueryFormTest {

    private static String queryFile = "/home/chris/chris1/scripts/snippets/SQL-queries";

    @BeforeClass
    public static void setUp() throws Exception {
        String inifile = "QueryDb.ini";
        if (new File(inifile).exists()) {
            MijnIni ini = new MijnIni(inifile);
            queryFile = ini.lees("Algemeen", "queryfile");
        }

    }


    @Test
    public void testLeesFile() {

        Map<String, String> info = new HashMap<String, String>();
        try {
            File file = new File(queryFile);
            BufferedReader in = new BufferedReader(new FileReader(file));

            QueryDb db = new QueryDb("/home/chris/IdeaProjects/java/QueryDb", "QueryDb.db");
            String categorie = "";
            String titel = "";
            StringBuffer tekst = new StringBuffer();
            String regel = in.readLine();
            boolean leesTitel = false;      // de volgende regel mag een titel zijn
            Query query = null;
            while (regel != null) {
                regel = in.readLine();

                // Lees de categorie
                Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
                Matcher mat = pat.matcher(regel);
                if(mat.find()) {
                    if (query != null && tekst != null && tekst.length() > 0) {
                        query.setTekst(tekst.toString());
                        db.schrijfQuery(query);
                    }
                    System.out.println("tekst1: " + tekst + "\n");
                    categorie = mat.group(1);

                    if (categorie.equals("bestellingen")) {
                        break;
                    }

                    query = new Query(categorie);
                    query.setTekst(tekst.toString());
                    System.out.println("Categorie: " + categorie);
                    tekst = new StringBuffer();
                } else if (leesTitel) {                     // Lees de titel
                    pat = Pattern.compile("^-+\\s*(.*)");
                    mat = pat.matcher(regel);
                    if(mat.find()) {
                        if (query != null && tekst.length() > 0) {
                            query.setTekst(tekst.toString());
                            db.schrijfQuery(query);
                        }
                        System.out.println("tekst2: " + tekst + "\n");
                        titel = mat.group(1);
                        System.out.println("Titel: " + titel);
                        info.put(titel, categorie);
                        tekst = new StringBuffer();
                        query.setTitel(titel);
                    }
                } else {
                    if (!regel.startsWith("}}}")) {
                        tekst.append(regel + "\n");
                    } else {
                        if (query != null && tekst != null && tekst.length() > 0) {
                            query.setTekst(tekst.toString());
                            db.schrijfQuery(query);
                        }
                    }
                }

//                if (regel.trim().equals("") || regel.startsWith("{{") || regel.startsWith("--")) {
                if (regel.trim().equals("") || regel.startsWith("{{")) {
                    leesTitel = true;
                } else {
                    leesTitel = false;
                }

            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (info.size() > 0) {
//            info.keySet()
            Set<String> categorien = new TreeSet<String>(info.values());
            for (Map.Entry<String, String> entry: info.entrySet()) {
                System.out.printf("Categorie: %s -> titel: %s\n", entry.getValue(), entry.getKey());
            }
        }

//        ArrayList<String> categorien = QueryForm.leesCategorien(info);

//        ArrayList<String> Titels = QueryForm.leesTitels(info, "admin functies");

    }

    @Test
    public void testParseQuery() {
        String tekst = "set @klantenid=90052148;\n" +
                "SELECT med.ais_record_id\n" +
                "FROM mediq.medicijnen med\n" +
                "JOIN `z-index`.artikelen a on med.atkode = a.zinummer\n" +
                "JOIN mediq.klanten k on med.klanten_id = k.id\n" +
                "JOIN mediq.apotheken apo ON k.apotheek_id = apo.id\n" +
                "WHERE med.proaktief = 'Y'\n" +
                "AND med.apotheek_bestelbaar = 'Y'\n" +
                "AND med.gewijzigd_door_klant = 'N'\n" +
                "AND med.herhaling_besteld='N'\n" +
                "AND med.klanten_id = @klantenid\n" +
                "AND DATE_ADD(med.herhalingsdatum, INTERVAL -63 DAY)  <=  '\".  @datum .\"'\n" +
                "AND klantstatus = 'A'\n" +
                "AND k.actief = 'Y'\n" +
                "GROUP BY med.id ;";
        if (tekst.contains("@klantenid")) {
            tekst = tekst.replace("@klantenid", "12345");
        }
        System.out.println(tekst);

    }

    @Test
    public void testFormatDatum() {
        String tekst = "where zoekdatum=@zoekdatum and vervaldatum=@vervaldatum and datum=@datum";
        if (tekst.contains("@zoekdatum")) {
            tekst = tekst.replace("@zoekdatum", formatDatum("12-03-2013", 1));
        }

        tekst = tekst.replaceAll("@[A-Z]*datum", formatDatum("12-03-2013", 2));
        System.out.println(tekst);

    }

    private String formatDatum(String datum, int format) {
        String result = "";
        if (format == 1) {
            result = datum.substring(6, 10) + datum.substring(3, 5) + datum.substring(0, 2);
        } else if (format == 2) {
            result = datum.substring(6, 10) + "-" + datum.substring(3, 5) + "-" +  datum.substring(0, 2);
        }

        return result;
    }

}
