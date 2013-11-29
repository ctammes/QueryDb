import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

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
    private JButton btnLezen;

    // Utility functies enzo
    Utility util;

    // Dit zijn de (nieuwe) waarden die opgeslagen moeten worden
    private int newId = 0;
    private String newCategorie = null;
    private String newTitel = null;
    private HashMap<String, String> variabelen;

    public OnderhoudsForm(String categorie, Titel titel, String tekst) {
        util = Utility.getInstance();
        util.vulCategorien(cmbCategorie);

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
                    util.vulTitels(categorie, cmbTitel);
                    Titel titel = (Titel) cmbTitel.getItemAt(0);
                    newTitel = titel.getTitel();
                    newId = titel.getId();
                    util.vulTekst(titel, txtQuery);
                }
            }
        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getSelectedIndex() == -1) {
                    newTitel = cmbTitel.getSelectedItem().toString();
                } else {
                    Titel titel = null;
                    if (cmbTitel.getSelectedItem() != null) {
                        titel = (Titel) cmbTitel.getSelectedItem();
                    } else {
                        titel = (Titel) cmbTitel.getItemAt(0);
                    }
                    newTitel = titel.getTitel();
                    newId = titel.getId();
                    util.vulTekst(titel, txtQuery);
                }
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = null;
                String msg = null;
                if (newId == -1) {
                    titel = new Titel(newId, newTitel);
                    msg = "<html>Er wordt een nieuwe query gemaakt: <br>" + newCategorie + "<br>" + newTitel + ".<br> Doorgaan ?<html>";
                } else {
                    titel = (Titel) cmbTitel.getSelectedItem();
                    msg = "Gegevens worden opgeslagen onder id " + titel.getId() + ". Doorgaan ?";
                    util.getDb().wijzigQueryTekst(titel, txtQuery.getText());
                }
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Gewijzigde tekst wordt opgeslagen", "info", JOptionPane.INFORMATION_MESSAGE);
                    if (newId == -1) {
                        util.getLog().info("Query toevoegen: " + titel.getTitel());
                    } else {
                        util.getDb().wijzigQueryTekst(titel, txtQuery.getText());
                        util.getLog().info("Query wijzigen: " + titel.getId() + " - " + titel.getTitel());
                    }
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
                Titel titel = (Titel) cmbTitel.getSelectedItem();
                String msg = "Je gaat de query met id " + titel.getId() + " verwijderen. Doorgaan ?";
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    JOptionPane.showMessageDialog(null, "Query wordt verwijderd", "info", JOptionPane.INFORMATION_MESSAGE);
                    util.getLog().info("Query verwijderen: " + titel.getId() + " - " + titel.getTitel());
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
        btnLezen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = (Titel) cmbTitel.getSelectedItem();
                util.vulPlainTekst(titel, txtQuery);
            }
        });
    }

    /**
     * Vul velden (bij instantiation vanuit hoofdscherm)
     * @param categorie
     * @param titel
     * @param tekst
     */
    protected void stelVeldenIn(String categorie, Titel titel, String tekst) {
        cmbCategorie.setSelectedItem(categorie);
        util.vulTitels(categorie, cmbTitel);
        // let op: getModel() ertussen, anders werkt het niet!
        cmbTitel.getModel().setSelectedItem(titel);
        txtQuery.setText(tekst);
    }
}
