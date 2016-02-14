import nl.ctammes.common.MijnIni;
import nl.ctammes.common.MijnLog;

import javax.swing.*;
import javax.swing.event.AncestorListener;
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
    private JComboBox cmbTaal;

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
    private JButton btnKlembord;
    private JButton btnOnderhoud;
    private JButton btnVariabelen;
    private JTextField txtDuur;
    private JTextField txtCcvId;

    private JMenuBar mnuBar1;
    private JMenu mnuBestanden;
    private JMenuItem mnuRefresh;
    private JMenuItem mnuAfsluiten;
    private JMenu mnuInitieel;
    private JMenuItem mnuKiesBestand;
    private JMenuItem mnuLeesBestand;
    private JMenu mnuExtra;
    private JMenuItem mnuVariabele;

    private static String queryFile = "/home/chris/scripts/snippets/SQL-queries";

    private static String dbDir = "/home/chris/IdeaProjects/java/QueryDb";
    private static String dbNaam = "QueryDb.db";

    private static JFrame frame = new JFrame("Snippets");

//    protected static QueryDb db = null;
//    private static Utility util = new Utility();

    static Utility util;

    private OnderhoudsForm onderhoudsform = null;

    private State currentState;     // huidige waarden
    private boolean parseVariabelen = true;

    // te vervangen variabelen
//    private HashMap<String, String> variabelen = null;

    // initialiseer logger
//    public static Logger log = Logger.getLogger(QueryForm.class.getName());

    private Map<String, String> info = null;

//TODO menu voor bestandsactie
//TODO onderhoudsvenster - nieuw: categorie is soms null
//TODO onderhoudsvenster - wijzigen: toevoegen van veld (@apotheek_agb) dat leeg in variabelevenster staat, lukt niet altijd. Wordt dan leeg getoond. (opgelost?)
//TODO onderhoudsvenster - (meermaals) wijzigen controleren
//TODO onderhoudsvenster - juiste titel en tekst tonen na toevoegen en verwijderen

    public QueryForm() {
        // Dialoogvenster voor wijzigen en toevoegen variabelen
//        util.getVariabeleDialog().pack();
//        util.getVariabeleDialog().setModal(true);
//        util.getVariabeleDialog().setVisible(false);

        // Initialisatie van het scherm
        txtFilenaam.setText(queryFile);

        txtDatum.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()).toString());
        doAction("txtdatum");

        // automatisch vullen van variabelen
        parseVariabelen = util.leesIni("Algemeen", "parsevariabelen", "1") == "1" ? true : false;

        //TODO ???
//        Titel titel = selectedTitel;

        // Sneltoetsen
        btnZoekTitel.setMnemonic('z');          // Alt-Z
        btnOnderhoud.setMnemonic('o');          // Alt-O
        btnKlembord.setMnemonic('k');           // Alt-K
        btnVariabelen.setMnemonic('v');         // Alt-V
        btnVervers.setMnemonic('e');            // Alt-E
        txtTekst.setFocusAccelerator('q');      // Alt-Q
        txtZoekTitel.setFocusAccelerator('t');  // Alt-T

        // Menu samenstellen
        mnuBar1 = new JMenuBar();

        mnuBestanden = new JMenu();
        mnuBestanden.setText("Bestanden");
        mnuBestanden.setMnemonic('b');

        mnuRefresh = new JMenuItem();
        mnuRefresh.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        mnuRefresh.setText("Refresh");
        mnuRefresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefreshActionPerformed(evt);
            }
        });
        mnuBestanden.add(mnuRefresh);

        mnuAfsluiten = new JMenuItem();
        mnuAfsluiten.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        mnuAfsluiten.setText("Afsluiten");
        mnuAfsluiten.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuAfsluitenActionPerformed(evt);
            }
        });
        mnuBestanden.add(mnuAfsluiten);

        mnuBar1.add(mnuBestanden);

        mnuInitieel = new JMenu();
        mnuInitieel.setText("Initieel");

        mnuKiesBestand = new JMenuItem();
        mnuKiesBestand.setText("Kies bestand");
        mnuKiesBestand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuKiesBestandActionPerformed(evt);
            }
        });
        mnuInitieel.add(mnuKiesBestand);

        mnuLeesBestand = new JMenuItem();
        mnuLeesBestand.setText("Lees bestand");
        mnuLeesBestand.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuLeesBestandActionPerformed(evt);
            }
        });
        mnuInitieel.add(mnuLeesBestand);

        mnuBar1.add(mnuInitieel);

        mnuExtra = new JMenu();
        mnuExtra.setText("Extra");
        mnuExtra.setMnemonic('x');

        mnuVariabele = new JCheckBoxMenuItem();
        mnuVariabele.setText("Vervang variabelen");
        mnuVariabele.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuVariabeleActionPerformed(evt);
            }
        });
        mnuVariabele.setSelected(parseVariabelen);
        mnuExtra.add(mnuVariabele);

        mnuBar1.add(mnuExtra);

        frame.setJMenuBar(mnuBar1);

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
                        util.vulCategorien(cmbCategorie, currentState.getTaal());
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
                    util.leesIni("Algemeen", "queryfile", txtFilenaam.getText());
                } else {
                    util.getLog().info("No Selection ");
                }
            }
        });

        cmbTaal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTaal.getItemCount() > 0) {        // onderdruk als lijst gewist wordt
                    Taal taal = null;
                    if (cmbTaal.getSelectedItem() != null) {
                        taal = (Taal) cmbTaal.getModel().getSelectedItem();
                    } else {
                        taal = (Taal) cmbTaal.getItemAt(0);
                    }
                    currentState.setTaal(taal);
                    updateCombo();
                }
            }
        });
        cmbCategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbCategorie.getItemCount()>0) {        // onderdruk als lijst gewist wordt
                    String categorie = null;
                    if (cmbCategorie.getSelectedItem() != null) {
                        categorie = cmbCategorie.getModel().getSelectedItem().toString();
                    } else {
                        categorie = cmbCategorie.getItemAt(0).toString();
                    }
                    currentState.setCategorie(categorie);

                    util.vulTitels(categorie, cmbTitel, currentState.getTaal());
                    Titel titel = (Titel) cmbTitel.getItemAt(0);
                    currentState.setTitel(titel);
                    util.vulTekst(titel, txtTekst, util.isGestart() && parseVariabelen);
                }
            }
        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getItemCount() > 0) {        // onderdruk als lijst gewist wordt
                    Titel titel = null;
                    if (cmbTitel.getSelectedItem() != null) {
                        titel = (Titel) cmbTitel.getModel().getSelectedItem();
                    } else {
                        titel = (Titel) cmbTitel.getItemAt(0);
                    }
                    util.vulTekst(titel, txtTekst, util.isGestart() && parseVariabelen);
                }
            }
        });

        btnLeesDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //TODO op een of andere manier de oorspronkelijke keuzes weer tonen; worden nu gereset
                util.vulTalen(cmbTaal);
                cmbTaal.getModel().setSelectedItem(currentState.getTaal());

                cmbCategorie.getModel().setSelectedItem(currentState.getCategorie());
                cmbTitel.getModel().setSelectedItem(currentState.getTitel());

            }
        });

        btnZoekTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String sleutel = txtZoekTitel.getText();
                if (sleutel.length() > 0) {
                    Object[] titels = zoekTitelsDb(sleutel);
                    if (titels.length > 0) {
                        DefaultComboBoxModel mod = new DefaultComboBoxModel(titels);
                        cmbTitel.setModel(mod);
                    } else {
                        JOptionPane.showMessageDialog(null, "Geen resultaten gevonden", "Informatie", JOptionPane.INFORMATION_MESSAGE);
                    }
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
        btnKlembord.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String tekst = txtTekst.getSelectedText();
                if (tekst == null) {
                    tekst = txtTekst.getText();
                }
                tekstNaarKlembord(tekst);
            }
        });
        btnOnderhoud.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (onderhoudsFrame == null){                   // initialisatie
                    onderhoudsFrame = new JFrame("OnderhoudsForm");
                    onderhoudsform = new OnderhoudsForm(currentState);
                    onderhoudsFrame.setContentPane(onderhoudsform.mainPanel);
                    onderhoudsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

                    String[] pos = util.leesIni("Diversen", "posonderhoud", "200,200").split(",");
                    onderhoudsFrame.setLocation(Integer.valueOf(pos[0]), Integer.valueOf(pos[1]));

//                    Integer id = -1;
//                    if (selectedTitel != null) {
//                        id = selectedTitel.getId();
//                    }
//                    onderhoudsFrame.setTitle("Query: " + id);
                    onderhoudsFrame.pack();
                    onderhoudsFrame.setVisible(true);
                } else if (!onderhoudsFrame.isShowing()) {      // hidden
                    onderhoudsform.stelVeldenIn(currentState);
//                    onderhoudsFrame.setTitle("Query: " + selectedTitel.getId());
                    onderhoudsFrame.setVisible(true);
                } else {                                        // heeft geen focus
                    onderhoudsform.stelVeldenIn(currentState);
//                    onderhoudsFrame.setTitle("Query: " + selectedTitel.getId());
                    onderhoudsFrame.toFront();
                }


            }
        });
        btnVariabelen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
//                util.toonVariabeleTable();
                doVariabeleAction();
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

        // Vullen van de comboboxen vanuit de database
        util.vulTalen(cmbTaal);
        currentState = new State((Taal) cmbTaal.getItemAt(0));
        updateCombo();
        util.vulTekst(currentState.getTitel(), txtTekst, util.isGestart() && parseVariabelen);
        currentState = new State((Taal) cmbTaal.getModel().getSelectedItem(), cmbCategorie.getModel().getSelectedItem().toString(), (Titel) cmbTitel.getModel().getSelectedItem(), txtTekst.getText());

    }

    private void mnuVariabeleActionPerformed(ActionEvent evt) {
        AbstractButton aButton = (AbstractButton) evt.getSource();
        parseVariabelen = aButton.getModel().isSelected();
        util.schrijfIni("Algemeen", "parsevariabelen", (parseVariabelen ? "1" : "0"));
    }

    /**
     * Bijwerken van de comboboxen als de taal gewijzigd is
     */
    private void updateCombo() {
        txtTekst.setText("");
        util.vulCategorien(cmbCategorie, currentState.getTaal());
        String categorie = null;
        Titel titel = null;
        if (cmbCategorie.getModel().getSize() > 0) {
            categorie = cmbCategorie.getItemAt(0).toString();
        }
        util.vulTitels(categorie, cmbTitel, currentState.getTaal());
        if (cmbTitel.getModel().getSize() > 0) {
            titel = (Titel) cmbTitel.getItemAt(0);
        }
        currentState.setCategorie(categorie);
        currentState.setTitel(titel);
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
     * Toon invulscherm voor variabelen
     */
    private void doVariabeleAction() {
        util.toonVariabeleTable();
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
                        query.setTaal(util.getDb().leesIdByTaal("SQL"));    // uit het SQL invoerbestand is alles 'SQL'
                        util.getDb().insertQuery(query);
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
                            util.getDb().insertQuery(query);
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

    private void mnuRefreshActionPerformed(ActionEvent evt) {
        //TODO op een of andere manier de oorspronkelijke keuzes weer tonen; worden nu gereset
        util.vulTalen(cmbTaal);
        cmbTaal.getModel().setSelectedItem(currentState.getTaal());

        cmbCategorie.getModel().setSelectedItem(currentState.getCategorie());
        cmbTitel.getModel().setSelectedItem(currentState.getTitel());
    }

    private void mnuAfsluitenActionPerformed(ActionEvent evt) {
        // TODO close action en afsluiten gelijktrekken. zie http://tips4java.wordpress.com/2009/05/01/closing-an-application/
        // TODO project stoppen bij afsluiten??
        util.schrijfIni("Diversen", "posquerydb", String.format("%d,%d",frame.getX(), frame.getY()));
        System.exit(0);
    }

    private void mnuKiesBestandActionPerformed(ActionEvent evt) {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(queryFile));
        fc.setDialogTitle("Selecteer query input bestand");
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            queryFile = fc.getSelectedFile().toString();
            util.schrijfIni("Algemeen", "queryfile", queryFile);
        } else {
            util.getLog().info("No Selection ");
        }
    }

    private void mnuLeesBestandActionPerformed(ActionEvent evt) {
        if (JOptionPane.showConfirmDialog(null, String.format("Database wordt leeggemaakt en opnieuw gevuld vanuit %s. Doorgaan?",queryFile), "Bevestig", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
            util.getDb().truncateQuery();
            leesFile();
            if (info.size() == 0) {
                String tekst = "Geen informatie gevonden in " + queryFile;
                JOptionPane.showMessageDialog(null, tekst, "Info", JOptionPane.INFORMATION_MESSAGE);
                util.getLog().info(tekst);
            } else {
                util.vulCategorien(cmbCategorie, currentState.getTaal());
            }
        }
    }



    /**
     * Zoek titels uit de database ahv zoeksleutel
     * @param sleutel
     * @return
     */
    private Object[] zoekTitelsDb(String sleutel) {
        return util.getDb().zoekTitels(sleutel, currentState.getTaal());
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
        util.initIni();

        String file = util.leesIni("Algemeen", "queryfile", queryFile);
        if (! file.equals(queryFile)) {
            util.schrijfIni("Algemeen", "queryfile", file);
            queryFile = file;
        }

        // Bij directory altijd forward slash gebruiken; werkt onder Linux en Windows
        String dir = util.leesIni("Algemeen", "dbdir", dbDir);
        if (! new File(dbDir).exists()) {
            dbDir = dir;
        } else {
            dbDir = dir;
        }
        util.schrijfIni("Algemeen", "dbdir", dbDir);

        String naam = util.leesIni("Algemeen", "dbnaam", dbNaam);
        if (! naam.equals(dbNaam)) {
            util.schrijfIni("Algemeen", "dbnaam", naam);
            dbNaam = naam;
        }

        util.setDb(new QueryDb(dbDir, dbNaam));

        frame.setContentPane(new QueryForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        String[] pos = util.leesIni("Diversen", "posquerydb", "200,200").split(",");
        frame.setLocation(Integer.valueOf(pos[0]), Integer.valueOf(pos[1]));
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

