import nl.ctammes.common.MijnIni;
import nl.ctammes.common.MijnLog;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
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
    private JTextField txtDatum;

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

        btnVerwerk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                leesFile();
                if (info.size() == 0) {
                    String tekst = "Geen informatie gevonden in " + queryFile;
                    JOptionPane.showMessageDialog(null, tekst, "Info", JOptionPane.INFORMATION_MESSAGE);
                    log.info(tekst);
                } else {
                    vulCategorien();
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
                    ArrayList<String> titels = leesTitelsDb(cmbCategorie.getSelectedItem().toString());
                    cmbTitel.removeAllItems();
                    for (String titel: titels) {
                        cmbTitel.addItem(titel);
                    }
                }
            }
        });

        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getSelectedItem() != null) {
                    titel = cmbTitel.getSelectedItem().toString();
                    System.out.println(categorie + " - " + titel);
                    String tekst = db.leesTekst(categorie, titel);
                    tekst = parseQuery(tekst);
                    txtTekst.setText(tekst);

                    StringSelection stringSelection = new StringSelection( tekst );
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents( stringSelection, stringSelection);
                }
            }
        });

        btnLeesDb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                vulCategorien();
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
                    if (query != null && tekst.length() > 0) {
                        query.setTekst(tekst.toString());
                        db.schrijfQuery(query);
                    }
                    pat = Pattern.compile("^-+\\s*(.*)");
                    mat = pat.matcher(regel);
                    if(mat.find()) {
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

    private ArrayList<String> leesTitelsDb(String categorie) {
        return db.leesTitels(categorie);
    }

    private void vulCategorien() {
        ArrayList<String> categorien = leesCategorienDb();
        cmbCategorie.removeAll();
        for (String categorie: categorien) {
            cmbCategorie.addItem(categorie);
        }
    }

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
            tekst = tekst.replaceAll("@[A-Z]datum", formatDatum(txtDatum.getText(), 2));
        }


        return tekst;
    }

    /**
     * Formatteer datum
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
        frame.pack();
        frame.setVisible(true);
    }
}
