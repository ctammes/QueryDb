import nl.ctammes.common.MijnIni;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
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
    private JTextField txtCategorie;
    private JTextField txtTitel;
    private JTextArea txtTekst;
    private JButton btnOpslaan;
    private JButton btnFileChooser;

    private MijnIni ini = null;
    private String inifile = "QueryDb.ini";


    public QueryForm() {
        btnLees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File queryFile = new File(txtFilenaam.getText());
                JOptionPane.showMessageDialog(null, queryFile, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnFileChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(txtFilenaam.getText()));
                fc.setDialogTitle("Selecteer Everpad database directory");
                fc.setDialogType(JFileChooser.OPEN_DIALOG);
                fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    txtFilenaam.setText(fc.getSelectedFile().toString());
                    if (ini == null) {
                        ini = new MijnIni(inifile);
                    }
                    ini.schrijf("Algemeen", "queryfile", txtFilenaam.getText());
                }
                else {
                    //Tomboy2Everpad.log.info("No Selection ");
                }
            }
        });
    }

    private void leesFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String regel = in.readLine();
            while (regel != null) {
                System.out.println(regel);
            }
            in.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * Lees de categorie
     */
    private String LeesCategorie(String tekst)  {
        String cat = "";
        Pattern pat = Pattern.compile("^\\{\\{\\{\\s*(.*)");
        Matcher mat = pat.matcher(tekst);
        if(mat.find()) {
            cat = mat.group(1);
        }
        return cat;
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("QueryForm");
        frame.setContentPane(new QueryForm().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
