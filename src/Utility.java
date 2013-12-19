import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by chris on 22-11-13.
 * Singleton class
 * Gedeelde code voor QueryForm project
 */
public final class Utility {

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
    private VariabeleTable variabeletable = null;

    private static final Utility utility = new Utility();

    private Utility()
    {
        System.err.println( "Utility object created." );
    }

    public static Utility getInstance()
    {
        return utility;
    } //

    /**
     * Vul queryvariabele of wijzig waarde
     * @param naam
     * @param waarde
     */
    void setVariabele(String naam, String waarde) {
        if (varlijst.contains(naam)) {
            varlijst.put(naam, waarde);
        } else {
            varlijst.add(new Variabele(naam, waarde));
        }
    }

    void removeVariabele(String naam) {
        varlijst.remove(naam);
    }

    /**
     * Lees queryvariabele
     * @param naam
     * @return
     */
    String getVariabele(String naam) {
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

    ArrayList<Variabele> getVariabelen() {
        return variabelen;
    }

    Logger getLog() {
        return log;
    }

    void setLog(Logger log) {
        this.log = log;
    }

    VariabeleDialog getVariabeledialog() { return variabeledialog; };

    void setDb(QueryDb db) {
        this.db = db;
    }

    QueryDb getDb() {
        return db;
    }

    /**
     * Vul de categorie combobox
     * @param combo
     */
    void vulCategorien(JComboBox combo) {
        int index = combo.getSelectedIndex();
        ArrayList<String> categorien = leesCategorienDb();
        combo.removeAllItems();
        for (String categorie: categorien) {
            if (categorie.trim() != "" ) {
                combo.addItem(categorie);
            }
        }
        if (index >= 0) {
            index = (index > combo.getItemCount()-1) ? combo.getItemCount()-1 : index;
            combo.setSelectedIndex(index);
        }
    }

    /**
     * Vul de titels combobox ahv. de categorie
     * @param categorie
     * @param combo
     */
    void vulTitels(String categorie, JComboBox combo) {
        ArrayList<Object> titels = leesTitelsDb(categorie);
        DefaultComboBoxModel mod=new DefaultComboBoxModel(titels.toArray());
        combo.setModel(mod);

    }

    /**
     * Vul de querytekst ahv. geselecteerde titel
     * @param titel
     * @param text
     */
    void vulTekst(Titel titel, JTextArea text){
        String tekst = db.leesTekstById(titel.getId());
        text.setText(parseQuery(tekst));
    }


    /**
     * Vul de querytekst ahv. geselecteerde titel direct uit db, zonder parse
     * @param titel
     * @param text
     */
    void vulPlainTekst(Titel titel, JTextArea text){
        String tekst = db.leesTekstById(titel.getId());
        text.setText(tekst);
    }


    /**
     * Lees de categorien uit de database
     * @return
     */
    ArrayList<String> leesCategorienDb() {
        return db.leesCategorien();
    }

    /**
     * Lees titels uit de database ahv. categorie
     * @param categorie
     * @return
     */
    ArrayList<Object> leesTitelsDb(String categorie) {
        return db.leesTitels(categorie);
    }

    /**
     * Vervang de veldnamen door waarden
     * @param tekst
     * @return
     */
    String parseQuery(String tekst) {
        // 'set @' eruit, behalve als er een (select ...) op volgt
        tekst = tekst.replaceAll("(?i)" + "set @[^;(]+;\\n", "");
        String waarde = "";
        // Variabelen eerst opzoeken en toevoegen aan de lijst
        Pattern pat = Pattern.compile("@([^@]+?)\\b");
        Matcher mat = pat.matcher(tekst);
        int start = 0;
        boolean toegevoegd = false;
        while (mat.find(start)) {
            String naam = mat.group(1);
            if (! varlijst.contains(naam)) {
                varlijst.add(new Variabele(naam, ""));
                toegevoegd = true;
            } else {
                if (varlijst.get(naam) == "") {     // toon ook als waarde niet is ingevuld
                    toegevoegd = true;
                }
            }
            start = mat.toMatchResult().end(1);
        }

        // Ontbrekende variabelen of variabelen zonder waarde laten invullen
        if (toegevoegd) {
            toonVariabeleTable();
        }

        // Variabelen vervolgens vervangen
        start = 0;
        while (mat.find(start)) {
            String naam = mat.group(1);
            waarde = varlijst.get(naam);
            if (waarde != "") {
                tekst = tekst.replace("@" + naam, waarde);
            }
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
    String formatDatum(String datum, int format) {
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

    /**
     * Vul de variabelen tabel vanuit het lijst object
     * @return
     */
    Object[][] vulVariabeleTableData() {
        // Initieel vullen met vaste vasriabelen
        if (varlijst.list().size() == 0) {
            varlijst.add(new Variabele("apotheek_id", true));
            varlijst.add(new Variabele("apotheek_agb", true));
            varlijst.add(new Variabele("ccv_id", true));
            varlijst.add(new Variabele("apotheeknaam", true));
            varlijst.add(new Variabele("klant_id", true));
            varlijst.add(new Variabele("klantenid", true));
            varlijst.add(new Variabele("ais_id", true));
            varlijst.add(new Variabele("zoekdatum", true));
            varlijst.add(new Variabele("datum", true));
            varlijst.add(new Variabele("tijd", true));
            varlijst.add(new Variabele("duur", true));
        }

        Object[][] data = new Object[varlijst.size()][2];
        int i = 0;
        for (Variabele var : varlijst.list()) {
            data[i][0] = var.getNaam();
            data[i][1] = var.getWaarde();
            i++;
        }

        return data;
    }

    /**
     * Vul het lijst object vanuit de variabelen tabel
     * @param data
     */
    void vulVariabeleLijstUitTable(Object[][] data) {
        if (data != null) {
            for (Object[] row : data) {
                if (!row[0].toString().equals("")) {
                    varlijst.put(row[0].toString(), row[1].toString());
                }
            }
        }
    }

    /**
     * Toon de tabel met variabelen
     */
     void toonVariabeleTable() {
        if (variabeletable == null) {
            variabeletable = new VariabeleTable();
            variabeletable.setPreferredSize(new Dimension(300,300));
            variabeletable.pack();
        } else {
            variabeletable.vulData();
        }

        variabeletable.setVisible(true);
        variabeletable.toFront();
    }


}
