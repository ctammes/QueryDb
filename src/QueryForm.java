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
    private JButton btnLees;
    private JPanel mainPanel;
    private JTextArea txtTekst;
    private JButton btnOpslaan;
    private JButton btnFileChooser;
    private JComboBox cmbCategorie;
    private JComboBox cmbTitel;

    private static MijnIni ini = null;
    private static String inifile = "QueryDb.ini";
    private static String queryFile = "/home/chris/scripts/snippets/SQL-queries";

    // initialiseer logger
    public static Logger log = Logger.getLogger(QueryForm.class.getName());

    private Map<String, String> info = null;

    public QueryForm() {
        txtFilenaam.setText(queryFile);

        btnLees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                leesFile();
                if (info.size() == 0) {
                    String tekst = "Geen informatie gevonden in " + queryFile;
                    JOptionPane.showMessageDialog(null, tekst, "Info", JOptionPane.INFORMATION_MESSAGE);
                    log.info(tekst);
                } else {
                    ArrayList<String> categorien = leesCategorien();
                    cmbCategorie.removeAll();
                    for (String categorie: categorien) {
                        cmbCategorie.addItem(categorie);
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
                ArrayList<String> titels = leesTitels(cmbCategorie.getSelectedItem().toString());
                cmbTitel.removeAllItems();
                for (String titel: titels) {
                    cmbTitel.addItem(titel);
                }
            }
        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

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
            String regel = in.readLine();
            boolean leesTitel = false;
            while (regel != null) {
                regel = in.readLine();

                // Lees de categorie
                Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
                Matcher mat = pat.matcher(regel);
                if(mat.find()) {
                    categorie = mat.group(1);
                }

                // Lees de titel
                if (leesTitel) {
                    pat = Pattern.compile("^-+\\s*(.*)");
                    mat = pat.matcher(regel);
                    if(mat.find()) {
                        info.put(mat.group(1), categorie);
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
            log = mijnlog.getLog();
            log.setLevel(Level.INFO);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (new File(inifile).exists()) {
            ini = new MijnIni(inifile);
            queryFile = ini.lees("Algemeen", "queryfile");
        }

        JFrame frame = new JFrame("QueryForm");
        frame.setContentPane(new QueryForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
