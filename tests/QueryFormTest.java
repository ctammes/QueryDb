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

            String categorie = "";
            String regel = in.readLine();
            while (regel != null) {
                regel = in.readLine();

                // Lees de categorie
                Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
                Matcher mat = pat.matcher(regel);
                if(mat.find()) {
                    categorie = mat.group(1);
//                    System.out.println("Categorie: " + categorie);
                }

                // Lees de titel
                pat = Pattern.compile("^-+\\s*(.*)");
                mat = pat.matcher(regel);
                if(mat.find()) {
//                    System.out.println("Titel: " + mat.group(1));
                    info.put(mat.group(1), categorie);
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

//        ArrayList<String> titels = QueryForm.leesTitels(info, "admin functies");

    }

}
