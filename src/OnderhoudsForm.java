
import javax.swing.*;
import java.awt.*;
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
    private JComboBox cmbTaal;

    // Utility functies enzo
    Utility util;

    // Dit zijn de (nieuwe) waarden die opgeslagen moeten worden
    private int newId = 0;
    private String newCategorie = null;
    private String newTitel = null;
    private String newTaal = null;
    private Taal selectedTaal;
    private HashMap<String, String> variabelen;

    // TODO wat doen bij selectie Categorie? Titels ook aanpassen??

    public OnderhoudsForm(String categorie, Titel titel, String tekst, final Taal taal) {
        util = Utility.getInstance();
        util.vulTalen(cmbTaal);
        selectedTaal = (Taal) taal;
        util.vulCategorien(cmbCategorie, selectedTaal);
        if (cmbCategorie.getModel().getSize() > 0) {
            util.vulTitels(cmbCategorie.getItemAt(0).toString(), cmbTitel, selectedTaal);
        }

        // om een of andere reden is het object niet zichtbaar als dit vanuuit de designer wordt gedaan
        cmbCategorie.setEditable(false);
        cmbTitel.setEditable(false);

        // Vul de velden
        stelVeldenIn(categorie, titel, tekst, taal);

        cmbTaal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbTaal.getItemCount() > 0) {        // onderdruk als lijst gewist wordt
                    Taal taal = null;
                    if (cmbTaal.getSelectedItem() != null) {
                        taal = (Taal) cmbTaal.getSelectedItem();
                    } else {
                        taal = (Taal) cmbTaal.getItemAt(0);
                    }
                    selectedTaal = taal;
                    System.out.println(selectedTaal.getTaal());
//                    util.vulTekst(titel, txtTekst, util.isGestart());
                }
            }
        });
        cmbCategorie.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbCategorie.getItemCount()>0 || newId == -1) {        // onderdruk als lijst gewist wordt
                    if (cmbCategorie.getSelectedIndex() == -1) {
                        newCategorie = cmbCategorie.getSelectedItem().toString();
                    } else {
                        String categorie = null;
                        if (cmbCategorie.getSelectedItem() != null) {
                            categorie = cmbCategorie.getSelectedItem().toString();
                        } else {
                            categorie = cmbCategorie.getItemAt(0).toString();
                        }
                        newCategorie = categorie;
                    }
                    util.vulTitels(newCategorie, cmbTitel, selectedTaal);
                    if (cmbTitel.getModel().getSize() > 0) {
                        Titel titel = (Titel) cmbTitel.getItemAt(0);
                        newTitel = titel.getTitel();
                        newId = newId != -1 ? titel.getId() : -1;

                        Window w = SwingUtilities.getWindowAncestor(mainPanel);
                        JFrame frame = (JFrame) w;
                        frame.setTitle("Query " + newId);

                        util.vulTekst(titel, txtQuery, false);
                    }
                }
            }
                        });
        cmbTitel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (cmbCategorie.getItemCount()>0 || newId == -1) {        // onderdruk als lijst gewist wordt
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
                        newId = newId != -1 ? titel.getId() : -1;

                        Window w = SwingUtilities.getWindowAncestor(mainPanel);
                        JFrame frame = (JFrame) w;
                        frame.setTitle("Query " + newId);

                        util.vulTekst(titel, txtQuery, false);
                    }
                }
            }
        });
        btnOpslaan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Titel titel = null;
                String msg = null;
                if (newId == -1) {
                    // TODO op nieuwe taal controleren
                    System.out.println(bestaatTaal(cmbTaal.getModel().getSelectedItem().toString()));
                    if (newCategorie != "" && newTitel != "" && txtQuery.getText() != "") {
                        titel = new Titel(newId, newTitel);
                        msg = "<html>Er wordt een nieuwe query gemaakt: <br>taal: " + ((newTaal != null) ? newTaal : selectedTaal.getTaal()) + "<br>categorie: " + newCategorie + "<br>titel: " + newTitel + ".<br> Doorgaan ?<html>";
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
                        Query query = new Query(newCategorie, newTitel, txtQuery.getText(), taal.getId());
                        newId = util.getDb().insertQuery(query);
                        cmbCategorie.setEditable(false);
                        cmbTitel.setEditable(false);
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
                txtQuery.setText("");
                newId = -1;

                Window w = SwingUtilities.getWindowAncestor(mainPanel);
                JFrame frame = (JFrame) w;
                frame.setTitle("Query " + newId);

                cmbCategorie.setEditable(true);
                cmbTitel.setEditable(true);

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
    protected void stelVeldenIn(String categorie, Titel titel, String tekst, Taal taal) {
        cmbCategorie.setSelectedItem(categorie);
        util.vulTitels(categorie, cmbTitel, taal);
        // let op: getModel() ertussen, anders werkt het niet!
        cmbTitel.getModel().setSelectedItem(titel);
        txtQuery.setText(tekst);
        newId = (titel != null) ? newId = titel.getId() : -1;
        util.vulTalen(cmbTaal);
        cmbTaal.getModel().setSelectedItem(taal);

//        if (mainPanel.isVisible()) {
//            Window w = SwingUtilities.getWindowAncestor(mainPanel);
//            JFrame frame = (JFrame) w;
//            frame.setTitle("Query " + newId);
//        }
    }

    protected boolean bestaatTaal(String taal) {
        boolean result = false;
        for (int i = 0; i < cmbTaal.getModel().getSize(); i++) {
            if (cmbTaal.getItemAt(i).toString() == taal) {
                result = true;
                break;
            }
        }
        return result;

    }

}
