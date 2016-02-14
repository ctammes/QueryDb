
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
    private JButton btnAfbreken;
    private JButton btnNieuw;
    private JButton btnVerwijderen;
    private JButton btnLezen;
    private JComboBox cmbTaal;

    // Utility functies enzo
    Utility util;

    // Dit zijn de (nieuwe) waarden die opgeslagen moeten worden
    private HashMap<String, String> variabelen;

    private State savedState;       // tbv. Afbreken - terugdraaien naar laatste state
    private State currentState;     // huidige waarden tbv. Opslaan

    // TODO wat doen bij selectie Categorie? Titels ook aanpassen??

    public OnderhoudsForm(State state) {
        savedState = new State(state);
        try {
            currentState = (State) savedState.clone();
        } catch (Exception e) {

        }

        util = Utility.getInstance();
        util.vulTalen(cmbTaal);
//        selectedTaal = (Taal) taal;

        // om een of andere reden is het object niet zichtbaar als dit vanuuit de designer wordt gedaan
        comboEditable(false);

        // Sneltoetsen
        btnNieuw.setMnemonic('n');
        btnOpslaan.setMnemonic('s');
        btnAfbreken.setMnemonic('a');
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
                    if (currentState != null) {
                        currentState.setTaal(taal);
                    }

                    cmbCategorie.removeAllItems();
                    cmbTitel.removeAllItems();
                    txtQuery.setText("");
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
                    if (currentState != null) {
                        currentState.setCategorie(categorie);

                    }
//                    queryId = queryId != -1 ? cmbCategorie.getSelectedIndex() : -1;

                    cmbTitel.removeAllItems();
                    txtQuery.setText("");
                    util.vulTitels(currentState.getCategorie(), cmbTitel, currentState.getTaal());
                    if (cmbTitel.getModel().getSize() > 0) {
                        Titel titel = (Titel) cmbTitel.getItemAt(0);
                        currentState.setTitel(titel);
//                        queryId = queryId != -1 ? titel.getId() : -1;

                        toonTitel(currentState.maakTitel());

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
                    if (currentState != null) {
                        currentState.setTitel(titel);
                    }

//                    queryId = titel.getId();
                    toonTitel(currentState.maakTitel());

                    util.vulTekst(titel, txtQuery, false);
                    currentState.setTekst(txtQuery.getText());
                }
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String msg = null;
                Integer taalId = 0;
                Taal taal = currentState.getTaal();
                String categorie = currentState.getCategorie();
                Titel titel = currentState.getTitel();
                String tekst = currentState.getTekst();

                // Nieuwe taal?
                if (taal.getId() == -1) {
                    taalId = util.insertTaalDb(currentState.getTaal().getTaal());
                } else {
                    taalId = taal.getId();
                }

                if (currentState.isNieuw()) {
                    if (!categorie.equals("") && categorie != null
                            && !titel.getTitel().equals("") && titel.getTitel() != null
                            && !tekst.equals("")) {
                                msg = "<html>Er wordt een nieuwe query gemaakt: <table><tr><td>taal: </td><td>" + taal.getTaal() + "</td></tr><tr><td>categorie: </td></td>" + categorie + "</td></tr><tr><td>titel: </td>" + titel.getTitel() + "</td></tr></table>Doorgaan ?<html>";
                    } else {
                        JOptionPane.showMessageDialog(null, "Niet alle velden ingevuld!", "Waarschuwing", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } else {
                    msg = "Gegevens worden opgeslagen onder id " + titel.getId() + ". Doorgaan ?";
                }
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    if (currentState.isNieuw()) {
                        util.getLog().info("Query toevoegen: " + titel.getId());
                        Query query = new Query(categorie, titel.getTitel(), tekst, taalId);
                        int id = util.insertQueryDb(query);
                        titel.setId(id);

                        comboEditable(false);
                    } else {
                        util.getLog().info("Query wijzigen: " + titel.getId() + " - " + titel.getTitel());
                        util.getDb().wijzigQueryTekst(titel, tekst);
                    }

                    // Bijwerken comboboxen
                    util.verversCombo(cmbCategorie, cmbTitel, taal, titel);
                    cmbCategorie.getModel().setSelectedItem(categorie);
                    cmbTitel.getModel().setSelectedItem(titel);
                    util.vulPlainTekst(titel, txtQuery);

                    currentState = new State(taal, categorie, titel, tekst);
                    savedState.setState(currentState);
                }
            }
        });
        btnAfbreken.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String msg;
                if (currentState.isNieuw()) {
                    msg = "Gegevens toevoegen afgebroken";
                } else {
                    msg = "Gegevens wijzigen afgebroken";
                }
                JOptionPane.showMessageDialog(null, msg, "info", JOptionPane.INFORMATION_MESSAGE);
                comboEditable(false);
                stelVeldenIn(savedState);
                currentState.setState(savedState);
//                if (queryId == -1 ) {
//                } else {
//                    util.vulPlainTekst(selectedTitel, txtQuery);
//                }
            }
        });
        btnVerwijderen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = currentState.getTitel();
                String msg = "Je gaat de query met id " + titel.getId() + " verwijderen. Doorgaan ?";
                if (JOptionPane.showConfirmDialog(null, msg, "Bevestig keuze", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
//                    int index = cmbTitel.getSelectedIndex();
                    util.getDb().verwijderQueryTekst(titel);
                    util.getLog().info("Query verwijderen: " + titel.getId() + " - " + titel.getTitel());
//                    index =  index < cmbTitel.getItemCount() ? index : index - 1;
                    util.verversCombo(cmbCategorie, cmbTitel, currentState.getTaal(), null);
                }
            }
        });
        btnNieuw.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                currentState.setNieuw(true);
                currentState.setTitel((Titel) cmbTitel.getModel().getSelectedItem());
                currentState.setCategorie(cmbCategorie.getModel().getSelectedItem().toString());
                currentState.setTekst("");
                toonTitel(currentState.maakTitel());

                // TODO nodig ???
                cmbTitel.getModel().setSelectedItem("");

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
        stelVeldenIn(currentState);

        txtQuery.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (currentState != null) {
                    currentState.setTekst(txtQuery.getText());
                }
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
        util.vulCategorien(cmbCategorie, currentState.getTaal());
        if (cmbCategorie.getModel().getSize() > 0) {
            util.vulTitels(cmbCategorie.getModel().getSelectedItem().toString(), cmbTitel, currentState.getTaal());
            txtQuery.setText("");
            if (cmbTitel.getModel().getSize() > 0) {
                util.vulTekst((Titel) cmbTitel.getModel().getSelectedItem(), txtQuery, false);
            }
            currentState.setTekst(txtQuery.getText());
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

    protected void toonTitel(String tekst) {
        Window w = SwingUtilities.getWindowAncestor(mainPanel);
        JFrame frame = (JFrame) w;
        // Overslaan tijdens initialisatie
        if (w != null) {
            frame.setTitle(tekst);
        }
    }

    /**
     * Vul velden (bij instantiation vanuit hoofdscherm)
     * @param state
     */
    protected void stelVeldenIn(State state) {
        util.vulTalen(cmbTaal);
        updateCombo();

        // let op: getModel() ertussen, anders werkt het niet!
        cmbTaal.getModel().setSelectedItem(state.getTaal());
        cmbCategorie.getModel().setSelectedItem(state.getCategorie());
//        util.vulTitels(categorie, cmbTitel, taal);
        cmbTitel.getModel().setSelectedItem(state.getTitel());
        txtQuery.setText(state.getTekst());
//        queryId = (titel != null) ? queryId = titel.getId() : -1;

        savedState = new State(state);
        try {
            currentState = (State) savedState.clone();
        } catch (Exception e) {

        }
    }

}
