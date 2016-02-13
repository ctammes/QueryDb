
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
    private int queryId = 0;
    private String newCategorie = null;
    private Taal selectedTaal = null;
    private Titel selectedTitel = null;
    private HashMap<String, String> variabelen;

    // TODO wat doen bij selectie Categorie? Titels ook aanpassen??

    public OnderhoudsForm(String categorie, Titel titel, String tekst, final Taal taal) {
        util = Utility.getInstance();
        util.vulTalen(cmbTaal);
        selectedTaal = (Taal) taal;

        // om een of andere reden is het object niet zichtbaar als dit vanuuit de designer wordt gedaan
        comboEditable(false);

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
                    queryId = queryId != -1 ? cmbCategorie.getSelectedIndex() : -1;

                    cmbTitel.removeAllItems();
                    txtQuery.setText("");
                    util.vulTitels(newCategorie, cmbTitel, selectedTaal);
                    if (cmbTitel.getModel().getSize() > 0) {
                        Titel titel = (Titel) cmbTitel.getItemAt(0);
                        selectedTitel = titel;
                        queryId = queryId != -1 ? titel.getId() : -1;

                        toonTitel(queryId);

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
                    // TODO waarom is titel een string? Hier wordt queryId op -1 gezet na opslaan, ten onrechte!
                    if (cmbTitel.getModel().getSelectedItem() instanceof String) {
                        titel = new Titel(-1, cmbTitel.getModel().getSelectedItem().toString());
                    } else if (cmbTitel.getModel().getSelectedItem() instanceof Titel) {
                        titel = (Titel) cmbTitel.getModel().getSelectedItem();
                    }
                    selectedTitel = titel;

                    queryId = titel.getId();
                    toonTitel(queryId);

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

                if (queryId == -1) {
                    if (!newCategorie.equals("") && newCategorie != null && !selectedTitel.getTitel().equals("") && selectedTitel.getTitel() != null && !txtQuery.getText().equals("")) {
                        msg = "<html>Er wordt een nieuwe query gemaakt: <br>taal: " + selectedTaal.getTaal() + "<br>categorie: " + newCategorie + "<br>titel: " + selectedTitel.getTitel() + ".<br> Doorgaan ?<html>";
                    } else {
                        JOptionPane.showMessageDialog(null, "Niet alle velden ingevuld!", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    msg = "Gegevens worden opgeslagen onder id " + selectedTitel.getId() + ". Doorgaan ?";
                }
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (queryId == -1) {
                        util.getLog().info("Query toevoegen: " + selectedTitel.getTitel());
                        Query query = new Query(newCategorie, selectedTitel.getTitel(), txtQuery.getText(), taalId);
                        queryId = util.insertQueryDb(query);

                        comboEditable(false);
                    } else {
                        util.getLog().info("Query wijzigen: " + selectedTitel.getId() + " - " + selectedTitel.getTitel());
                        util.getDb().wijzigQueryTekst(titel, txtQuery.getText());
                    }

                    // Bijwerken comboboxen
                    util.verversCombo(cmbCategorie, cmbTitel, selectedTaal, selectedTitel);
                    cmbCategorie.getModel().setSelectedItem(newCategorie);
                    cmbTitel.getModel().setSelectedItem(selectedTitel);
                    util.vulPlainTekst(selectedTitel, txtQuery);

                }
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String msg;
                if (queryId == -1) {
                    msg = "Gegevens toevoegen afgebroken";
                } else {
                    msg = "Gegevens wijzigen afgebroken";
                }
                JOptionPane.showMessageDialog(null, msg, "info", JOptionPane.INFORMATION_MESSAGE);
                comboEditable(false);
                if (queryId == -1 ) {
                    // TODO iets doen met queryId -1
                } else {
                    util.vulPlainTekst(selectedTitel, txtQuery);
                }
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
                queryId = -1;
                toonTitel(queryId);

                cmbTitel.getModel().setSelectedItem("");
                txtQuery.setText("");

                newCategorie = cmbCategorie.getModel().getSelectedItem().toString();
                selectedTitel = (Titel) cmbTitel.getModel().getSelectedItem();

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

        // Vul de velden met de inhoud uit het vorige venster
        // Aan het einde van de cnstructor, anders kloppen de comboboxen niet, die moeten er eerst zijn
        stelVeldenIn(categorie, titel, tekst, taal);

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
    protected void toonTitel(Integer id) {
        Window w = SwingUtilities.getWindowAncestor(mainPanel);
        JFrame frame = (JFrame) w;
        // Overslaan tijdens initialisatie
        if (w != null) {
            frame.setTitle("Query " + (id == -1 ? "nieuw" : id));
        }
    }

    /**
     * Vul velden (bij instantiation vanuit hoofdscherm)
     * @param categorie
     * @param titel
     * @param tekst
     */
    protected void stelVeldenIn(String categorie, Titel titel, String tekst, Taal taal) {
        util.vulTalen(cmbTaal);
        updateCombo();

        // let op: getModel() ertussen, anders werkt het niet!
        cmbTaal.getModel().setSelectedItem(taal);
        cmbCategorie.getModel().setSelectedItem(categorie);
//        util.vulTitels(categorie, cmbTitel, taal);
        cmbTitel.getModel().setSelectedItem(titel);
        txtQuery.setText(tekst);
        queryId = (titel != null) ? queryId = titel.getId() : -1;
    }

}
