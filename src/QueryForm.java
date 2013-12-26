import nl.ctammes.common.MijnIni;
import nl.ctammes.common.MijnLog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO '@'als odnerdeel van email adres moet niet vervangen worden!

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 1-3-13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class QueryForm {
    private JTextArea txtTekst;
    private JComboBox cmbCategorie;
    private JComboBox cmbTitel;

    private JFrame onderhoudsFrame;

    private JTextField txtFilenaam;
    private JButton btnVerwerk;
    private JPanel mainPanel;
    private JButton btnFileChooser;
    private JButton btnLeesDb;
    private JTextField txtApotheekId;
    private JTextField txtApotheekAgb;
    private JTextField txtApotheekNaam;
    private JTextField txtKlantenId;
    private JTextField txtKlant_id;
    private JTextField txtAis_id;
    private JFormattedTextField txtDatum;
    private JTextField txtZoekTitel;
    private JButton btnZoekTitel;
    private JButton btnVervers;
    private JButton btnklembord;
    private JButton btnOnderhoud;
    private JButton btnVariabelen;
    private JTextField txtDuur;
    private JTextField txtCcvId;

    private static MijnIni ini = null;
    private static String inifile = "QueryDb.ini";
    private static String queryFile = "/home/chris/scripts/snippets/SQL-queries";

    private static String dbDir = "/home/chris/IdeaProjects/java/QueryDb";
    private static String dbNaam = "QueryDb.db";

//    protected static QueryDb db = null;
//    private static Utility util = new Utility();

    static Utility util;

    private OnderhoudsForm onderhoudsform = null;

    private String selectedCategorie;
    private Titel selectedTitel;
    private Integer queryId;

    // te vervangen variabelen
//    private HashMap<String, String> variabelen = null;

    // initialiseer logger
//    public static Logger log = Logger.getLogger(QueryForm.class.getName());

    private Map<String, String> info = null;

//TODO menu voor bestandsactie

    public QueryForm() {
        // Dialoogvenster voor wijzigen en toevoegen variabelen
        util.getVariabeledialog().pack();
        util.getVariabeledialog().setModal(true);
        util.getVariabeledialog().setVisible(false);

        // Initialisatie van het scherm
        txtFilenaam.setText(queryFile);

        txtDatum.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()).toString());
        doAction("txtdatum");

        // Vullen van de comboboxen vanuit de database
        util.vulCategorien(cmbCategorie);
        selectedCategorie = cmbCategorie.getItemAt(0).toString();
        util.vulTitels(selectedCategorie, cmbTitel);
        selectedTitel = (Titel) cmbTitel.getItemAt(0);
        //TODO ???
//        Titel titel = selectedTitel;
        util.vulTekst(selectedTitel, txtTekst, util.isGestart());

        btnVerwerk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (JOptionPane.showConfirmDialog(null, "Database wordt leeggemaakt. Doorgaan?", "Bevestig", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
                    btnVerwerk.setEnabled(false);
                    util.getDb().truncateQuery();
                    leesFile();
                    btnVerwerk.setEnabled(true);
                    if (info.size() == 0) {
                        String tekst = "Geen informatie gevonden in " + queryFile;
                        JOptionPane.showMessageDialog(null, tekst, "Info", JOptionPane.INFORMATION_MESSAGE);
                        util.getLog().info(tekst);
                    } else {
                        util.vulCategorien(cmbCategorie);
                    }
                }
            }
        });

        btnFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(txtFilenaam.getText()));
                fc.setDialogTitle("Selecteer query input bestand");
                fc.setDialogType(JFileChooser.OPEN_DIALOG);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    txtFilenaam.setText(fc.getSelectedFile().toString());
                    queryFile = txtFilenaam.getText();
                    if (ini == null) {
                        ini = new MijnIni(inifile);
                        util.getLog().info("Inifile " + inifile + " aangemaakt");
                    }
                    ini.schrijf("Algemeen", "queryfile", txtFilenaam.getText());
                } else {
                    util.getLog().info("No Selection ");
                }
            }
        });

        cmbCategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbCategorie.getItemCount()>0) {        // onderdruk als lijst gewist wordt
                    String categorie = null;
                    if (cmbCategorie.getSelectedItem() != null) {
                        categorie = cmbCategorie.getSelectedItem().toString();
                    } else {
                        categorie = cmbCategorie.getItemAt(0).toString();
                    }
                    selectedCategorie = categorie;
                    util.vulTitels(categorie, cmbTitel);
                    Titel titel = (Titel) cmbTitel.getItemAt(0);
                    selectedTitel = titel;
                    util.vulTekst(titel, txtTekst, util.isGestart());
                }
            }
        });

        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getItemCount() > 0) {        // onderdruk als lijst gewist wordt
                    Titel titel = null;
                    if (cmbTitel.getSelectedItem() != null) {
                        titel = (Titel) cmbTitel.getSelectedItem();
                    } else {
                        titel = (Titel) cmbTitel.getItemAt(0);
                    }
                    selectedTitel = titel;
                    util.vulTekst(titel, txtTekst, util.isGestart());
                }
            }
        });

        btnLeesDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                util.vulCategorien(cmbCategorie);
            }
        });

        btnZoekTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String sleutel = txtZoekTitel.getText();
                if (sleutel.length() > 0) {
                    Object[] titels = zoekTitelsDb(sleutel);
                    DefaultComboBoxModel mod=new DefaultComboBoxModel(titels);
                    cmbTitel.setModel(mod);
                }
            }
        });
        btnVervers.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String tekst = txtTekst.getText();
                txtTekst.setText(util.parseQuery(tekst, util.isGestart()));
                tekstNaarKlembord(tekst);
            }
        });
        btnklembord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                tekstNaarKlembord(txtTekst.getText());
            }
        });
        btnOnderhoud.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (onderhoudsFrame == null){                   // initialisatie
                    onderhoudsFrame = new JFrame("OnderhoudsForm");
                    onderhoudsform = new OnderhoudsForm(selectedCategorie, selectedTitel, txtTekst.getText());
                    onderhoudsFrame.setContentPane(onderhoudsform.mainPanel);
                    onderhoudsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
                    onderhoudsFrame.setLocation(100, 100);
                    onderhoudsFrame.pack();
                    onderhoudsFrame.setVisible(true);
                } else if (!onderhoudsFrame.isShowing()) {      // hidden
                    onderhoudsform.stelVeldenIn(selectedCategorie, selectedTitel, txtTekst.getText());
                    onderhoudsFrame.setVisible(true);
                } else {                                        // heeft geen focus
                    onderhoudsform.stelVeldenIn(selectedCategorie, selectedTitel, txtTekst.getText());
                    onderhoudsFrame.toFront();
                }


            }
        });
        btnVariabelen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                util.toonVariabeleTable();
            }
        });

        txtApotheekId.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("apotheek_id", txtApotheekId.getText());
            }
        });
        txtApotheekNaam.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("apotheeknaam", txtApotheekNaam.getText());
            }
        });
        txtApotheekAgb.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("apotheek_agb", txtApotheekAgb.getText());
            }
        });
        txtKlant_id.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("klant_id", txtKlant_id.getText());
            }
        });
        txtKlantenId.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("klantenid", txtKlantenId.getText());
            }
        });
        txtAis_id.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("ais_id", txtAis_id.getText());
            }
        });

        txtDatum.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }
            @Override
            public void focusLost(FocusEvent focusEvent) {
                doAction("txtdatum");
            }
        });
        txtDuur.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                util.setVariabele("duur", txtDuur.getText());
            }
        });

        txtCcvId.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent focusEvent) {

            }

            @Override
            public void focusLost(FocusEvent focusEvent) {
                util.setVariabele("ccv_id", txtCcvId.getText());
            }
        });
    }

    /**
     * Actie gekoppeld aan een tekstveld
     * @param naam
     */
    private void doAction(String naam) {
        if (naam == "txtdatum") {
            util.setVariabele("zoekdatum", util.formatDatum(txtDatum.getText(), 1));
            util.setVariabele("datum", util.formatDatum(txtDatum.getText(), 2));
            util.setVariabele("timestamp", util.formatDatum(txtDatum.getText(), 3));
        }

    }

    /**
     * Doorloop querybestand en sla categorie/titel op
     */
    private void leesFile() {
        info = new HashMap<String, String>();
        try {
            File file = new File(queryFile);
            BufferedReader in = new BufferedReader(new FileReader(file));

            String categorie = "";
            String titel = "";
            StringBuffer tekst = new StringBuffer();
            String regel = in.readLine();
            boolean leesTitel = false;
            Query query = null;
            while (regel != null) {
                regel = in.readLine();

                // Lees de categorie
                Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
                Matcher mat = pat.matcher(regel);
                if(mat.find()) {
                    if (query != null && tekst != null && tekst.length() > 0) {
                        query.setTekst(tekst.toString());
                        util.getDb().schrijfQuery(query);
                    }
                    categorie = mat.group(1);
                    query = new Query(categorie);
                    query.setTekst(tekst.toString());
                    tekst = new StringBuffer();
                } else if (leesTitel) {                     // Lees de titel
                    pat = Pattern.compile("^-+\\s*(.*)");
                    mat = pat.matcher(regel);
                    if(mat.find()) {
                        if (query != null && tekst.length() > 0) {
                            query.setTekst(tekst.toString());
                            util.getDb().schrijfQuery(query);
                        }
                        titel = mat.group(1);
                        info.put(titel, categorie);
                        tekst = new StringBuffer();
                        query.setTitel(titel);
                    }
                } else {
                    if (!regel.startsWith("}}}")) {
                        tekst.append(regel + "\n");
                    }
                }

                if (regel.trim().equals("") || regel.startsWith("{{")) {
                    leesTitel = true;
                } else {
                    leesTitel = false;
                }

            }


        } catch (Exception e) {
            util.getLog().severe(e.getMessage());
        }

    }

    /**
     * Zoek titels uit de database ahv zoeksleutel
     * @param sleutel
     * @return
     */
    private Object[] zoekTitelsDb(String sleutel) {
        return util.getDb().zoekTitels(sleutel);
    }

    /**
     * Stuur querytekst naar het system klembord
     */
    private void tekstNaarKlembord(String tekst) {
        if (tekst.length() > 0) {
            StringSelection stringSelection = new StringSelection(tekst);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents( stringSelection, stringSelection);
        }
    }

    /**
     * Lees de categorien
     * @return
     */
    private ArrayList<String> leesCategorien() {
        TreeSet<String> categorien = new TreeSet<String>(info.values());
        return new ArrayList<String>(categorien);
    }

    /**
     * Lees de titels bij een categorie
     * @param categorie
     * @return
     */
    private ArrayList<String> leesTitels(String categorie) {
        ArrayList<String> titels = new ArrayList<String>();
        for (Map.Entry<String, String> entry: info.entrySet()) {
            if (entry.getValue().equals(categorie)) {
                titels.add(entry.getKey());
            }
        }
        return titels;

    }

    /**
     * Geef de tekst in de categorie combobox
     * @return
     */
    protected String getCategorie() {
        return selectedCategorie;
    }


    /**
     * Geef de tekst in de titel combobox
     * @return
     */
    protected Titel getTitel() {
        return selectedTitel;
    }

    public static void main(String[] args) {
        String logDir = ".";
        String logNaam = "QueryDb.log";

        try {
            MijnLog mijnlog = new MijnLog(logDir, logNaam, true);
            util = Utility.getInstance();
            util.setLog(mijnlog.getLog());
            util.getLog().setLevel(Level.INFO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // inifile lezen of initieel vullen
        if (new File(inifile).exists()) {
            ini = new MijnIni(inifile);
            queryFile = ini.lees("Algemeen", "queryfile");
            String dir = ini.lees("Algemeen", "dbdir");
            if (dir != null) {
                dbDir = dir;
            } else {
                if (new File(dbDir).exists()) {
                    ini.schrijf("Algemeen", "dbdir", dbDir);
                }
            }
            String naam = ini.lees("Algemeen", "dbnaam");
            if (naam != null) {
                dbNaam = naam;
            } else {
                ini.schrijf("Algemeen", "dbnaam", dbNaam);
            }
        } else {
            ini = new MijnIni(inifile);
            ini.schrijf("Algemeen", "queryfile", queryFile);
            ini.schrijf("Algemeen", "dbdir", dbDir);
            ini.schrijf("Algemeen", "dbnaam", dbNaam);
            util.getLog().info("Inifile " + inifile + " aangemaakt en gevuld");
        }
        util.setDb(new QueryDb(dbDir, dbNaam));

        JFrame frame = new JFrame("QueryForm");
        frame.setContentPane(new QueryForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100,100);
        frame.pack();
        frame.setVisible(true);

        util.setGestart(true);

    }

    private void createUIComponents() {
        txtDatum = new JFormattedTextField(new SimpleDateFormat("dd-MM-yyyy"));
    }

    // Test om de JTextFields in een enkele functie van een listerner te voorzien
    // Lukt zo in ieder geval niet
    // getName() geeft nooit de juiste naam
    private void test(Container con) {
        for (Component com : con.getComponents()) {
            if (com instanceof JTextField) {
                System.out.println("textveld: " + ((JTextField) com).getName());    // + com.toString());
            } else {
                test((Container) com);
            }
        }

    }



}

