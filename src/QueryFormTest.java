import junit.framework.TestCase;
import sun.misc.Regexp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 4-3-13
 * Time: 7:03
 * To change this template use File | Settings | File Templates.
 */
public class QueryFormTest extends TestCase {

    public void testLeesFile() {
        String dir = "/home/chris/chris1/scripts/snippets";
        String filenaam = "SQL-queries";

        try {
            File file = new File(dir + "/" + filenaam);
            BufferedReader in = new BufferedReader(new FileReader(file));

            String regel = in.readLine();
            while (regel != null) {
                regel = in.readLine();
//                if (regel.matches("^\\{\\{\\{.*")) {
//                    System.out.println("Categorie: " + regel);
//                }

                // Lees de categorie
                Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
                Matcher mat = pat.matcher(regel);
                if(mat.find())
                    System.out.println("Categorie: " + mat.group(1));


                // Lees de titel
                pat = Pattern.compile("^-+\\s*(.*)");
                mat = pat.matcher(regel);
                if(mat.find())
                    System.out.println("Titel: " + mat.group(1));

            }





        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
