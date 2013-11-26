import javax.swing.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 22-11-13.
 * Gedeelde code voor QueryForm project
 */
public class Utility {

    // Database
    private QueryDb db;

    // Variabelen in de query (@...)
    // key   = naam (zonder @)
    // value = waarde
//    private HashMap<String, String> variabelen = new HashMap<String, String>();
    private ArrayList<Variabele> variabelen = new ArrayList<Variabele>();
    private VariabeleLijst varlijst = new VariabeleLijst();

    // Initialiseer logger
    private Logger log = Logger.getLogger(QueryForm.class.getName());

    private VariabeleDialog variabeledialog = new VariabeleDialog();

    /**
     * Vul queryvariabele
     * @param naam
     * @param waarde
     */
    public void setVariabele(String naam, String waarde) {
//        variabelen.put(naam, waarde);
        varlijst.add(new Variabele(naam, waarde));
    }

    /**
     * Lees queryvariabele
     * @param naam
     * @return
     */
    public String getVariabele(String naam) {
        String waarde = "";
        for (Variabele var : variabelen) {
            if (var.getNaam() == naam) {
                waarde = var.getWaarde();
                break;
            }
        }
        return waarde;
//        return variabelen.get(naam);
    }

    public ArrayList<Variabele> getVariabelen() {
        return variabelen;
    }

    public Logger getLog() {
        return log;
    }

    public void setLog(Logger log) {
        this.log = log;
    }

    protected VariabeleDialog getVariabeledialog() { return variabeledialog; };

    protected void setDb(QueryDb db) {
        this.db = db;
    }

    protected QueryDb getDb() {
        return db;
    }

    /**
     * Vul de categorie combobox
     * @param combo
     */
    protected void vulCategorien(JComboBox combo) {
        ArrayList<String> categorien = leesCategorienDb();
        combo.removeAll();
        for (String categorie: categorien) {
            if (categorie.trim() != "" ) {
                combo.addItem(categorie);
            }
        }
    }

    /**
     * Vul de titels combobox ahv. de categorie
     * @param categorie
     * @param combo
     */
    protected void vulTitels(String categorie, JComboBox combo) {
        ArrayList<Object> titels = leesTitelsDb(categorie);
        DefaultComboBoxModel mod=new DefaultComboBoxModel(titels.toArray());
        combo.setModel(mod);

    }

    /**
     * Vul de querytekst ahv. geselecteerde titel
     * @param titel
     * @param text
     */
    protected void vulTekst(Titel titel, JTextArea text){
        String tekst = db.leesTekstById(titel.getId());
        text.setText(parseQuery(tekst));
    }


    /**
     * Lees de categorien uit de database
     * @return
     */
    protected ArrayList<String> leesCategorienDb() {
        return db.leesCategorien();
    }

    /**
     * Lees titels uit de database ahv. categorie
     * @param categorie
     * @return
     */
    protected ArrayList<Object> leesTitelsDb(String categorie) {
        return db.leesTitels(categorie);
    }

    /**
     * Vervang de veldnamen door waarden
     * @param tekst
     * @return
     */
    protected String parseQuery(String tekst) {
        // 'set @' eruit
        tekst = tekst.replaceAll("(?i)" + "^set @[^;]+;", "");
        String waarde = "";
        // Variabelen opzoeken en verwerken
        Pattern pat = Pattern.compile("@([^@]+?)\\b");
        Matcher mat = pat.matcher(tekst);
        int start = 0;
        while (mat.find(start)) {
            String naam = mat.group(1);
//            for (Variabele var : varlijst.list()) {
//                if (var.getNaam() == naam) {
//                    waarde = var.getWaarde();
//                }
//            }

            if (varlijst.contains(naam)) {
                waarde = varlijst.get(naam);
            } else {
                variabeledialog.setTxtNaam(naam);
                variabeledialog.setVisible(true);
                variabeledialog.toFront();
                waarde=variabeledialog.getTxtWaarde();
                varlijst.add(new Variabele(naam, waarde));
            }

//            if (variabelen.contains(naam)) {
//                waarde = variabelen.get(variabelen.indexOf(naam)).toString();
//            } else {
//                variabeledialog.setTxtNaam(naam);
//                variabeledialog.setVisible(true);
//                variabeledialog.toFront();
//                waarde=variabeledialog.getTxtWaarde();
//                variabelen.add(new Variabele(naam, waarde));
//            }
            tekst = tekst.replace("@" + naam, waarde);

            start = mat.toMatchResult().end(1);
        }
        return tekst;
    }

    /**
     * Formatteer de datum afhankelijk van het veld
     * @param datum dd-mm-jjjj
     * @param format 1=zoekdatum 2=omgekeerd 3=timestamp
     * @return
     */
    protected String formatDatum(String datum, int format) {
        String result = "";
        if (format == 1) {
            result = datum.substring(6, 10) + datum.substring(3, 5) + datum.substring(0, 2);
        } else if (format == 2) {
            result = datum.substring(6, 10) + "-" + datum.substring(3, 5) + "-" +  datum.substring(0, 2);
        } else if (format == 3) {
            result = datum.substring(6, 10) + "-" + datum.substring(3, 5) + "-" +  datum.substring(0, 2) + " " + datum.substring(11, 13) + ":" + datum.substring(14, 16) + ":" +  datum.substring(17, 19);
        }

        return result;
    }




}
