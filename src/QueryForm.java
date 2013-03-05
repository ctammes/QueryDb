import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.jar.JarInputStream;
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
    private JPanel queryPanel;
    private JTextField txtCategorie;
    private JTextField txtTitel;
    private JTextArea txtTekst;
    private JButton btnOpslaan;


    public QueryForm() {
        btnLees.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                File queryFile = new File(txtFilenaam.getText());
                JOptionPane.showMessageDialog(null, queryFile, "Info", JOptionPane.INFORMATION_MESSAGE);
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
        frame.setContentPane(new QueryForm().queryPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
