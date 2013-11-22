import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JButton btnNieuw;
    private JButton btnVerwijderen;

    QueryForm form = new QueryForm();

    // Dit zijn de (nieuwe) waarden die opgeslagen moeten worden
    private int newId = 0;
    private String newCategorie = null;
    private String newTitel = null;

    public OnderhoudsForm(String categorie, Titels titel, String tekst) {
        parent.vulCategorien(cmbCategorie);

        // Vul de velden
        stelVeldenIn(categorie, titel, tekst);

        cmbCategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getSelectedIndex() == -1) {
                    newCategorie = cmbCategorie.getSelectedItem().toString();
                } else {
                    String categorie = null;
                    if (cmbCategorie.getSelectedItem() != null) {
                        categorie = cmbCategorie.getSelectedItem().toString();
                    } else {
                        categorie = cmbCategorie.getItemAt(0).toString();
                    }
                    newCategorie = categorie;
                    form.vulTitels(categorie, cmbTitel);
                    Titels titel = (Titels) cmbTitel.getItemAt(0);
                    newTitel = titel.getTitel();
                    newId = titel.getId();
                    form.vulTekst(titel, txtQuery);
                }
            }
        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getSelectedIndex() == -1) {
                    newTitel = cmbTitel.getSelectedItem().toString();
                } else {
                    Titels titel = null;
                    if (cmbTitel.getSelectedItem() != null) {
                        titel = (Titels) cmbTitel.getSelectedItem();
                    } else {
                        titel = (Titels) cmbTitel.getItemAt(0);
                    }
                    newTitel = titel.getTitel();
                    newId = titel.getId();
                    form.vulTekst(titel, txtQuery);
                }
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titels titel = null;
                String msg = null;
                if (newId == -1) {
                    titel = new Titels(newId, newTitel);
                    msg = "<html>Er wordt een nieuwe query gemaakt: <br>" + newCategorie + "<br>" + newTitel + ".<br> Doorgaan ?<html>";
                } else {
                    titel = (Titels) cmbTitel.getSelectedItem();
                    msg = "Gegevens worden opgeslagen onder id " + titel.getId() + ". Doorgaan ?";
                }
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Gewijzigde tekst wordt opgeslagen", "info", JOptionPane.INFORMATION_MESSAGE);
                    form.log.info("Query wijzigen: " + titel.getId() + " - " + titel.getTitel());
                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Gegevens wijzigen afgebroken", "info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnVerwijderen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titels titel = (Titels) cmbTitel.getSelectedItem();
                String msg = "Je gaat de query met id " + titel.getId() + " verwijderen. Doorgaan ?";
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Query wordt verwijderd", "info", JOptionPane.INFORMATION_MESSAGE);
                    form.log.info("Query verwijderen: " + titel.getId() + " - " + titel.getTitel());
                }
            }
        });
        btnNieuw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Nieuwe query toevoegen", "info", JOptionPane.INFORMATION_MESSAGE);
                txtQuery.setText("");
                newId = -1;
            }
        });
    }

    /**
     * Vul velden (bij aanroep vanuit hoofdscherm)
     * @param categorie
     * @param titel
     * @param tekst
     */
    public void stelVeldenIn(String categorie, Titels titel, String tekst) {
        cmbCategorie.setSelectedItem(categorie);
        form.vulTitels(categorie, cmbTitel);
        // let op: getModel() ertussen, anders werkt het niet!
        cmbTitel.getModel().setSelectedItem(titel);
        txtQuery.setText(tekst);
    }
}
