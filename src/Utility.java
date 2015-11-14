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
    private static boolean gestart = false;     // is applicatie gestart (ivm. tonen variabele venster)?

    private Utility()
    {
//        System.err.println( "Utility object created." );
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

    public static boolean isGestart() {
        return gestart;
    }

    public static void setGestart(boolean gestart) {
        Utility.gestart = gestart;
    }

    /**
     * Ververs de comboboxen na wijzigingen
     * @param categorie
     * @param combo
     * @param taal
     * @param titel
     */
    void verversCombo(JComboBox categorie, JComboBox combo, Taal taal, Titel titel) {
        vulCategorien(categorie, taal);
        if (categorie.getSelectedItem() != null) {
            if (titel != null) {
                vulTitels(categorie.getSelectedItem().toString(), combo, taal, titel);
            } else {
                vulTitels(categorie.getItemAt(0).toString(), combo, taal);
            }
        } else {
            vulTitels(categorie.getItemAt(0).toString(), combo, taal);
        }

    }

    /**
     * Vul de talen combobox
     * @param combo
     */
    void vulTalen(JComboBox combo) {
        ArrayList<Object> talen = leesTalenDb();
        DefaultComboBoxModel mod=new DefaultComboBoxModel(talen.toArray());
        combo.setModel(mod);
    }

    /**
     * Vul de categorie combobox
     * @param combo
     * @param taal
     */
    void vulCategorien(JComboBox combo, Taal taal) {
        int index = combo.getSelectedIndex();
        ArrayList<String> categorien = leesCategorienDb(taal);
        combo.removeAllItems();
        for (String categorie: categorien) {
            if (categorie.trim() != "" ) {
                combo.addItem(categorie);
            }
        }
        // Selecteer juiste entry (-1 = nieuw toegevoegd)
        if (index != -1) {
            index = (index > combo.getItemCount()-1) ? combo.getItemCount()-1 : index;
            combo.setSelectedIndex(index);
        }
    }

    /**
     * Vul de titels combobox ahv. de categorie
     * @param categorie
     * @param combo
     * @param taal
     */
    void vulTitels(String categorie, JComboBox combo, Taal taal) {
        combo.removeAllItems();
        if (categorie != null) {
            ArrayList<Object> titels = leesTitelsDb(categorie, taal);
            DefaultComboBoxModel mod = new DefaultComboBoxModel(titels.toArray());
            combo.setModel(mod);
        }
    }

    /**
     * Vul de titels combobox ahv. de categorie en selecteer een entry
     * @param categorie
     * @param combo
     * @param taal
     * @param titel
     */
    void vulTitels(String categorie, JComboBox combo, Taal taal, Titel titel) {
        vulTitels(categorie, combo, taal);
        //TODO toont juiste index nog niet
        combo.setSelectedItem(titel.getTitel());
    }

    /**
     * Vul de querytekst ahv. geselecteerde titel
     * @param titel
     * @param text
     */
    void vulTekst(Titel titel, JTextArea text, boolean vraagVariabele){
        if (titel != null) {
            String tekst = db.leesTekstById(titel.getId());
            text.setText(parseQuery(tekst, vraagVariabele));
        } else {
            text.setText("");
        }
    }


    /**
     * Vul de querytekst ahv. geselecteerde titel direct uit db, zonder parse
     * @param titel
     * @param text
     */
    void vulPlainTekst(Titel titel, JTextArea text){
        if (titel != null) {
            String tekst = db.leesTekstById(titel.getId());
            text.setText(tekst);
        } else {
            text.setText("");
        }
    }

    /**
     * Lees titel object ahv. de id (nodig bij toevoegen record)
     * @param id
     * @return
     */
    Titel leesTitel(int id) {
        Titel result = null;
        try {
            return db.leesTitelById(id);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return result;

    }


    /**
     * Lees de categorien uit de database
     * @return
     */
    ArrayList<String> leesCategorienDb(Taal taal) {
        return db.leesCategorieen(taal);
    }

    /**
     * Lees titels uit de database ahv. categorie
     * @param categorie
     * @return
     */
    ArrayList<Object> leesTitelsDb(String categorie, Taal taal) {
        return db.leesTitels(categorie, taal);
    }

    /**
     * Lees de talen uit de database
     * @return
     */
    ArrayList<Object> leesTalenDb() {
        return db.leesTalen();
    }

    /**
     * Vervang de veldnamen door waarden
     * @param tekst
     * @return
     */
    String parseQuery(String tekst, boolean vraagWaarde) {
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
                if (varlijst.get(naam).equals("")) {     // toon ook als waarde niet is ingevuld
                    toegevoegd = true;
                }
            }
            start = mat.toMatchResult().end(1);
        }

        // Ontbrekende variabelen of variabelen zonder waarde laten invullen
        if (vraagWaarde && toegevoegd) {
            toonVariabeleTable();
        }

        // Variabelen vervolgens vervangen
        start = 0;
        while (mat.find(start)) {
            String naam = mat.group(1);
            waarde = varlijst.get(naam);
            if (!waarde.equals("")) {
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
     * (onderdruk tijdens starten applicatie)
     */
     void toonVariabeleTable() {
        if (isGestart()) {
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


}
