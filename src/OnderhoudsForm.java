
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
    private JComboBox cmbTaal;

    // Utility functies enzo
    Utility util;

    // Dit zijn de (nieuwe) waarden die opgeslagen moeten worden
    private int newId = 0;
    private String newCategorie = null;
    private String newTitel = null;
    private Taal selectedTaal = null;
    private HashMap<String, String> variabelen;

    // TODO wat doen bij selectie Categorie? Titels ook aanpassen??

    public OnderhoudsForm(String categorie, Titel titel, String tekst, final Taal taal) {
        util = Utility.getInstance();
        util.vulTalen(cmbTaal);
        selectedTaal = (Taal) taal;

        // om een of andere reden is het object niet zichtbaar als dit vanuuit de designer wordt gedaan
        comboEditable(false);

        // Vul de velden met de inhoud uit het vorige venster
        stelVeldenIn(categorie, titel, tekst, taal);

        // Sneltoetsen
        btnNieuw.setMnemonic('n');
        btnOpslaan.setMnemonic('s');
        btnCancel.setMnemonic('a');
        btnVerwijderen.setMnemonic('w');
        btnLezen.setMnemonic('l');
        txtQuery.setFocusAccelerator('q');

        cmbTaal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTaal.getModel().getSelectedItem() != null) {        // onderdruk als lijst gewist wordt
                    Taal taal = null;
                    if (cmbTaal.getModel().getSelectedItem() instanceof Taal) {
                        if (cmbTaal.getModel().getSelectedItem() != null) {
                            taal = (Taal) cmbTaal.getModel().getSelectedItem();
                        } else {
                            taal = (Taal) cmbTaal.getItemAt(0);
                        }
                    } else {
                        // Nieuwe taal toegevoegd
                        taal = new Taal(-1, cmbTaal.getModel().getSelectedItem().toString());
                    }
                    selectedTaal = taal;

                    cmbCategorie.removeAllItems();
                    cmbTitel.removeAllItems();
                    txtQuery.setText("");
//                    util.vulCategorien(cmbCategorie, selectedTaal);
                    updateCombo();
//                    util.vulTekst(titel, txtTekst, util.isGestart());
                }
            }
        });
        cmbCategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbCategorie.getModel().getSelectedItem() != null) {        // onderdruk als lijst gewist wordt
                    String categorie = null;
                    if (cmbCategorie.getModel().getSelectedItem() instanceof String) {
                        categorie = cmbCategorie.getModel().getSelectedItem().toString();
                    }
                    newCategorie = categorie;
                    newId = newId != -1 ? cmbCategorie.getSelectedIndex() : -1;

                    cmbTitel.removeAllItems();
                    txtQuery.setText("");
                    util.vulTitels(newCategorie, cmbTitel, selectedTaal);
                    if (cmbTitel.getModel().getSize() > 0) {
                        Titel titel = (Titel) cmbTitel.getItemAt(0);
                        newTitel = titel.getTitel();
                        newId = newId != -1 ? titel.getId() : -1;

                        toonTitel(newId);

                        util.vulTekst(titel, txtQuery, false);
                    }
                }
            }
                        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTitel.getModel().getSelectedItem() != null) {        // onderdruk als lijst gewist wordt
                    Titel titel = null;
                    if (cmbTitel.getModel().getSelectedItem() instanceof String) {
                        titel = new Titel(-1, cmbTitel.getModel().getSelectedItem().toString());
                    } else if (cmbTitel.getModel().getSelectedItem() instanceof Titel) {
                        titel = (Titel) cmbTitel.getModel().getSelectedItem();
                    }
                    newId = titel.getId();
                    newTitel = titel.getTitel();

                    toonTitel(newId);

                    util.vulTekst(titel, txtQuery, false);
                }
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = null;
                String msg = null;
                Integer taalId = 0;

                // Nieuwe taal?
                if (selectedTaal.getId() == -1) {
                    taalId = util.insertTaalDb(selectedTaal.getTaal());
                } else {
                    taalId = selectedTaal.getId();
                }

                if (newId == -1) {
                    if (newCategorie != "" && newCategorie != null && newTitel != "" && newTitel != null && txtQuery.getText() != "") {
                        titel = new Titel(newId, newTitel);
                        msg = "<html>Er wordt een nieuwe query gemaakt: <br>taal: " + selectedTaal.getTaal() + "<br>categorie: " + newCategorie + "<br>titel: " + newTitel + ".<br> Doorgaan ?<html>";
                    } else {
                        JOptionPane.showMessageDialog(null, "Niet alle velden ingevuld!", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    titel = (Titel) cmbTitel.getSelectedItem();
                    msg = "Gegevens worden opgeslagen onder id " + titel.getId() + ". Doorgaan ?";
                }
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (newId == -1) {
                        util.getLog().info("Query toevoegen: " + titel.getTitel());
                        Query query = new Query(newCategorie, newTitel, txtQuery.getText(), taalId);
                        newId = util.insertQueryDb(query);

                        comboEditable(false);
                    } else {
                        util.getLog().info("Query wijzigen: " + titel.getId() + " - " + titel.getTitel());
                        util.getDb().wijzigQueryTekst(titel, txtQuery.getText());
                    }
                    // Bijwerken comboboxen
                    titel = util.leesTitel(newId);
                    util.verversCombo(cmbCategorie, cmbTitel, selectedTaal, titel);
                    cmbCategorie.setSelectedItem(newCategorie);
                    cmbTitel.setSelectedItem(titel.getTitel());
                    util.vulPlainTekst(titel, txtQuery);

                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JOptionPane.showMessageDialog(null, "Gegevens wijzigen afgebroken", "info", JOptionPane.INFORMATION_MESSAGE);
                comboEditable(false);
                // TODO iets doen met newId, die kan -1 zijn
            }
        });
        btnVerwijderen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = (Titel) cmbTitel.getSelectedItem();
                String msg = "Je gaat de query met id " + titel.getId() + " verwijderen. Doorgaan ?";
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    int index = cmbTitel.getSelectedIndex();
                    util.getDb().verwijderQueryTekst(titel);
                    util.getLog().info("Query verwijderen: " + titel.getId() + " - " + titel.getTitel());
                    index =  index < cmbTitel.getItemCount() ? index : index - 1;
                    util.verversCombo(cmbCategorie, cmbTitel, selectedTaal, null);
                }
            }
        });
        btnNieuw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                newId = -1;
                toonTitel(newId);

                cmbTitel.getModel().setSelectedItem("");
                txtQuery.setText("");

                newCategorie = cmbCategorie.getModel().getSelectedItem().toString();
                newTitel = cmbTitel.getModel().getSelectedItem().toString();

                comboEditable(true);

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


    public void windowClosing(WindowEvent e) {
        Window w = SwingUtilities.getWindowAncestor(mainPanel);
        util.schrijfIni("Diversen", "posonderhoud", String.format("%d,%d",w.getX(), w.getY()));
    }

    /**
     * Bijwerken van de comboboxen als de taal gewijzigd is
     */
    private void updateCombo() {
        util.vulCategorien(cmbCategorie, selectedTaal);
        if (cmbCategorie.getModel().getSize() > 0) {
            util.vulTitels(cmbCategorie.getModel().getSelectedItem().toString(), cmbTitel, selectedTaal);
            txtQuery.setText("");
            if (cmbTitel.getModel().getSize() > 0) {
                util.vulTekst((Titel) cmbTitel.getModel().getSelectedItem(), txtQuery, false);
            }
        }

    }

    /**
     * Maak de comboboxen (un)editable
      * @param status wel of niet editable
     */
    private void comboEditable(boolean status) {
        cmbTaal.setEditable(status);
        cmbCategorie.setEditable(status);
        cmbTitel.setEditable(status);
    }

    /**
     * Toon de id in de titelbalk
     * @param id
     */
    private void toonTitel(Integer id) {
        Window w = SwingUtilities.getWindowAncestor(mainPanel);
        JFrame frame = (JFrame) w;
        frame.setTitle("Query " + (id == -1 ? "nieuw" : id));
    }

    /**
     * Vul velden (bij instantiation vanuit hoofdscherm)
     * @param categorie
     * @param titel
     * @param tekst
     */
    protected void stelVeldenIn(String categorie, Titel titel, String tekst, Taal taal) {
        util.vulTalen(cmbTaal);
        // let op: getModel() ertussen, anders werkt het niet!
        cmbTaal.getModel().setSelectedItem(taal);
        cmbCategorie.getModel().setSelectedItem(categorie);
//        util.vulTitels(categorie, cmbTitel, taal);
        cmbTitel.getModel().setSelectedItem(titel);
        txtQuery.setText(tekst);
        newId = (titel != null) ? newId = titel.getId() : -1;

    }

}
