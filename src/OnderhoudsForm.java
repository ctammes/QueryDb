import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created with IntelliJ IDEA.
 * User: chris
 * Date: 26-5-13
 * Time: 20:41
 * To change this template use File | Settings | File Templates.
 */
public class OnderhoudsForm {
    protected JPanel mainPanel;
    private JTextArea txtQuery;
    private JComboBox cmbCategorie;
    private JComboBox cmbTitel;
    private JButton btnOpslaan;
    private JButton btnCancel;

    QueryForm form = new QueryForm();

    public OnderhoudsForm() {
        cmbCategorie.removeAll();
        for (int i=0; i<form.cmbCategorie.getItemCount(); i++) {
            cmbCategorie.addItem(form.cmbCategorie.getItemAt(i));
        }

        cmbTitel.removeAll();
        for (int i=0; i<form.cmbTitel.getItemCount(); i++) {
            cmbTitel.addItem(form.cmbTitel.getItemAt(i));
        }

        cmbCategorie.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                if (cmbCategorie.getSelectedItem() != null) {
                    form.vulTitels(cmbCategorie.getSelectedItem().toString(), cmbTitel);
                } else {
                    form.vulTitels(cmbCategorie.getItemAt(0).toString(), cmbTitel);
                }
                Titels titel = (Titels) cmbTitel.getItemAt(0);
                form.vulTekst(titel, txtQuery);
            }
        });

        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titels titel = null;
                if (cmbTitel.getSelectedItem() != null) {
                    titel = (Titels) cmbTitel.getSelectedItem();
                } else {
                    titel = (Titels) cmbTitel.getItemAt(0);
                }
                form.vulTekst(titel, txtQuery);
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Gegevens worden opgeslagen", "info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Gegevens wijzigen afgebroken", "info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
