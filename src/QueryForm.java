import nl.ctammes.common.MijnIni;
import nl.ctammes.common.MijnLog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 1-3-13
 * Time: 16:21
 * To change this template use File | Settings | File Templates.
 */
public class QueryForm {
    private JTextField txtFilenaam;
    private JButton btnVerwerk;
    private JPanel mainPanel;
    private JTextArea txtTekst;
    private JButton btnFileChooser;
    private JComboBox cmbCategorie;
    private JComboBox cmbTitel;
    private JButton btnLeesDb;
    private JTextField txtApotheekId;
    private JTextField txtApotheekAgb;
    private JTextField txtApotheekNaam;
    private JTextField txtKlantId;
    private JTextField txtKlantKlant_id;
    private JTextField txtKlantAis_id;
    private JFormattedTextField txtDatum;
    private JTextField txtZoekTitel;
    private JButton btnZoekTitel;

    private static MijnIni ini = null;
    private static String inifile = "QueryDb.ini";
    private static String queryFile = "/home/chris/scripts/snippets/SQL-queries";

    private static String dbDir = "/home/chris/IdeaProjects/java/QueryDb";
    private static String dbNaam = "QueryDb.db";

    private static QueryDb db = null;
    private String categorie;
    private String titel;

    // initialiseer logger
    public static Logger log = Logger.getLogger(QueryForm.class.getName());

    private Map<String, String> info = null;

    public QueryForm() {
        txtFilenaam.setText(queryFile);
        txtDatum.setText(new SimpleDateFormat("dd-MM-yyyy").format(new Date()).toString());

        btnVerwerk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (JOptionPane.showConfirmDialog(null, "Database wordt leeggemaakt. Doorgaan?", "Bevestig", JOptionPane.YES_NO_CANCEL_OPTION) == JOptionPane.YES_OPTION) {
                    btnVerwerk.setEnabled(false);
                    db.truncateQuery();
                    leesFile();
                    btnVerwerk.setEnabled(true);
                    if (info.size() == 0) {
                        String tekst = "Geen informatie gevonden in " + queryFile;
                        JOptionPane.showMessageDialog(null, tekst, "Info", JOptionPane.INFORMATION_MESSAGE);
                        log.info(tekst);
                    } else {
                        vulCategorien();
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
                    if (ini == null) {
                        ini = new MijnIni(inifile);
                        log.info("Inifile " + inifile + " aangemaakt");
                    }
                    ini.schrijf("Algemeen", "queryfile", txtFilenaam.getText());
                } else {
                    log.info("No Selection ");
                }
            }
        });

        cmbCategorie.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (cmbCategorie.getSelectedItem() != null) {
                    categorie = cmbCategorie.getSelectedItem().toString();
                    ArrayList<Object> titels = zoekTitelsDbTest(cmbCategorie.getSelectedItem().toString());
                    ArrayList<Titels> titels1 = zoekTitelsDbTest1(cmbCategorie.getSelectedItem().toString());
//                    Titels[] titels = leesTitelsDb(cmbCategorie.getSelectedItem().toString());
//                    Vector model = new Vector();
//                    Object[] model = new Object[]{titels};
//                    int i = 0;
//                    for (Map.Entry titel: titels.entrySet()) {
//                    for (Titels titel: titels) {
//                        System.out.println(titel.getKey().toString() + " - " + titel.getValue().toString());
//                        System.out.println(titel.getId().toString() + " - " + titel.getTitel().toString());
//                        model.addElement(titel);
//                    }
                    for (Titels titel: titels1) {
                        System.out.println(titel.getId());
                    }


                    DefaultComboBoxModel mod = new DefaultComboBoxModel(titels.toArray());
                    cmbTitel.setModel(mod);


//                    cmbTitel.removeAllItems();
//                    for (Map.Entry titel: titels.entrySet()) {
//                        System.out.println(titel.getKey().toString() + " - " + titel.getValue().toString());
//                        cmbTitel.addItem(new Titels(Integer.valueOf(titel.getKey().toString()), titel.getValue().toString()));
//                    }
                }
            }
        });

        cmbTitel.addActionListener(new ActionListener() {
            @Override

            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getSelectedItem() != null) {
                    Titels titel = (Titels) cmbTitel.getSelectedItem();
                    int id = titel.getId();
                    System.out.println("selected: " + titel.getId());
                    String tekst = db.leesTekstById(id);
                    txtTekst.setText(tekst);
//                    System.out.println(titel.getId() + " - " + titel.getTitel());
                }
/*
                if (cmbTitel.getSelectedItem() != null) {
                    int i = cmbTitel.getSelectedIndex();
                    Titels titel1 = (Titels) cmbTitel.getItemAt(i);
//                    Titels titel1 = (Titels) cmbTitel.getSelectedItem();
                    System.out.printf("%d - %s\n", titel1.getId(), titel1.getTitel());
                    String tekst = db.leesTekstById(titel1.getId());
//                    titel = cmbTitel.getSelectedItem().toString();
//                    System.out.println(categorie + " - " + titel);
//                    String tekst = db.leesTekst(categorie, titel);
                    tekst = parseQuery(tekst);
                    txtTekst.setText(tekst);

                    StringSelection stringSelection = new StringSelection( tekst );
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents( stringSelection, stringSelection);
                }
*/
            }
        });

        btnLeesDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                vulCategorien();
            }
        });

        btnZoekTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String sleutel = txtZoekTitel.getText();
                if (sleutel.length() > 0) {
                    ArrayList<Object> titels = zoekTitelsDbTest(sleutel);
                    ArrayList<Titels> titels1 = zoekTitelsDbTest1(sleutel);
                    Object[] titels2 = zoekTitelsDbTest2(sleutel);

                    System.out.println("Objecten:");
                    for (Object titel: titels) {
                        Titels titel1 = (Titels) titel;
                        System.out.println(titel1.getId() + " - " + titel1.getTitel());
                    }

//                    System.out.println("Objecten2:");
//                    for (Object titel: titels2) {
//                        System.out.println(titel.getId() + " - " + titel.getTitel());
//                    }
                    DefaultComboBoxModel mod=new DefaultComboBoxModel(titels2);
                    cmbTitel.setModel(mod);

                    System.out.println("Titels:");
                    for (Titels titel: titels1) {
                        System.out.println(titel.getId() + " - " + titel.getTitel());
                    }

//                    DefaultComboBoxModel mod=new DefaultComboBoxModel(titels);
//                    jComboBox1.setModel(mod);


//                    ArrayList<String> titels =  zoekTitelsDb(sleutel);
//                    cmbTitel.removeAllItems();
//                    for (Titels titel: titels1) {
//                        cmbTitel.addItem(titel.getTitel());
//                    }
                }
            }
        });
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
                        db.schrijfQuery(query);
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
                            db.schrijfQuery(query);
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
            log.severe(e.getMessage());
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

    private ArrayList<String> leesCategorienDb() {
        return db.leesCategorien();
    }

    /**
     * Lees de Titels bij een categorie
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

    private Titels[] leesTitelsDb(String categorie) {
        return db.leesTitels(categorie);
    }

    private ArrayList<String> zoekTitelsDb(String sleutel) {
        return db.zoekTitels(sleutel);
    }

    private ArrayList<Object> zoekTitelsDbTest(String sleutel) {
        return db.zoekTitelsTest(sleutel);
    }

    private ArrayList<Titels> zoekTitelsDbTest1(String sleutel) {
        return db.zoekTitelsTest1(sleutel);
    }

    private Object[] zoekTitelsDbTest2(String sleutel) {
        return db.zoekTitelsTest2(sleutel);
    }

    private void vulCategorien() {
        ArrayList<String> categorien = leesCategorienDb();
        cmbCategorie.removeAll();
        for (String categorie: categorien) {
            cmbCategorie.addItem(categorie);
        }
    }

    /**
     * Vervang de veldnamen door waarden
     * @param tekst
     * @return
     */
    private String parseQuery(String tekst) {
        if (tekst.contains("@apotheek_id")) {
            tekst = tekst.replace("@apotheek_id", txtApotheekId.getText());
        }
        if (tekst.contains("@agb_code")) {
            tekst = tekst.replace("@agb_code", txtApotheekAgb.getText());
        }
        if (tekst.contains("@apotheeknaam")) {
            tekst = tekst.replace("@apotheeknaam", txtApotheekNaam.getText());
        }

        if (tekst.contains("@klantenid")) {
            tekst = tekst.replace("@klantenid", txtKlantId.getText());
        }
        if (tekst.contains("@klant_id")) {
            tekst = tekst.replace("@klant_id", txtKlantKlant_id.getText());
        }
        if (tekst.contains("@ais_id")) {
            tekst = tekst.replace("@ais_id", txtKlantAis_id.getText());
        }

        if (tekst.contains("@zoekdatum")) {
            tekst = tekst.replace("@zoekdatum", formatDatum(txtDatum.getText(), 1));
        }

        if (txtDatum.getText().length()>0) {
            tekst = tekst.replaceAll("@[A-Z]*datum", formatDatum(txtDatum.getText(), 2));
        }


        return tekst;
    }

    /**
     * Formatteer de datum afhankelijk van het veld
     * @param datum dd-mm-jjjj
     * @param format 1=zoekdatum 2=omgekeerd
     * @return
     */
    private String formatDatum(String datum, int format) {
        String result = "";
        if (format == 1) {
            result = datum.substring(6, 10) + datum.substring(3, 5) + datum.substring(0, 2);
        } else if (format == 2) {
            result = datum.substring(6, 10) + "-" + datum.substring(3, 5) + "-" +  datum.substring(0, 2);
        }

        return result;
    }

    public static void main(String[] args) {
        String logDir = ".";
        String logNaam = "QueryDb.log";

        try {
            MijnLog mijnlog = new MijnLog(logDir, logNaam, true);
            log = mijnlog.getLog();
            log.setLevel(Level.INFO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (new File(inifile).exists()) {
            ini = new MijnIni(inifile);
            queryFile = ini.lees("Algemeen", "queryfile");
        }

        db = new QueryDb(dbDir, dbNaam);

        JFrame frame = new JFrame("QueryForm");
        frame.setContentPane(new QueryForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100,100);
        frame.pack();
        frame.setVisible(true);
    }

    private void createUIComponents() {
        txtDatum = new JFormattedTextField(new SimpleDateFormat("dd-MM-yyyy"));
    }
}

