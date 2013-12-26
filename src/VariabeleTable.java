import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.*;

public class VariabeleTable extends JDialog implements TableModelListener, KeyListener {
    private JPanel contentPane;
    private JButton btnOk;
    private JButton btnAfbreken;
    private JTable tblVariabele;
    private JButton button1;

    Utility util;

    public VariabeleTable()  {
        util = Utility.getInstance();
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btnOk);

        // TODO kan dit weg?
        tblVariabele.addKeyListener(this);

        VariabeleModel mod = new VariabeleModel();
        tblVariabele.setModel(mod);
        tblVariabele.getModel().addTableModelListener(this);

        btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Nodig om een end edit event af te vuren (setValueAt)
                if (tblVariabele.isEditing())
                    tblVariabele.getCellEditor().stopCellEditing();
                onOK();
            }
        });

        btnAfbreken.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Nodig om een end edit event af te vuren (setValueAt)
                if (tblVariabele.isEditing())
                    tblVariabele.getCellEditor().stopCellEditing();
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        int rijen = tblVariabele.getModel().getRowCount();
        Object[][] data = new Object[rijen][2];
        for (int i=0; i<rijen; i++) {
            data[i][0] = tblVariabele.getModel().getValueAt(i,0);
            data[i][1] = tblVariabele.getModel().getValueAt(i,1);
        }
        util.vulVariabeleLijstUitTable(data);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    /**
     * Verwijder de regel met 'naam' uit de tabel en de variabele lijst
     * @param naam
     */
    private void removeNaam(String naam) {
//        VariabeleModel mod = (VariabeleModel) tblVariabele.getModel();
//        System.out.println(mod.getRowCount());
//        System.out.println(mod.getValueAt(4, 0));
//        util.removeVariabele(naam);
//        mod.removeRow(4);

        util.removeVariabele(naam);
        vulData();
        tblVariabele.repaint();
    }

    /**
     * Onderdeel van de listener
     * @param e
     */
    public void tableChanged(TableModelEvent e) {
        int row = e.getFirstRow();
        int column = e.getColumn();
        if (column >= 0) {
            TableModel model = (TableModel)e.getSource();
            Object naam = model.getValueAt(row, 0);
            Object waarde = model.getValueAt(row, column);
        }
    }

    public void vulData() {
        VariabeleModel mod = (VariabeleModel) tblVariabele.getModel();
        mod.data = util.vulVariabeleTableData();
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    /**
     * Verwijderen van een variabele met de Delete toets
     * @param keyEvent
     */
    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_DELETE && tblVariabele.getSelectedRow() != -1) {
            String msg = tblVariabele.getValueAt(tblVariabele.getSelectedRow(), 0) + " wordt verwijderd";
            if (JOptionPane.showConfirmDialog(null, msg, "Bevestig", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                removeNaam(tblVariabele.getValueAt(tblVariabele.getSelectedRow(), 0).toString());
//                removeRow(tblVariabele.getSelectedRow());
            }
        } else if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            onOK();
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }

//    public static void main(String[] args) {
//        VariabeleTable dialog = new VariabeleTable();
//        dialog.setPreferredSize(new Dimension(300,300));
//        dialog.pack();
//        dialog.setVisible(true);
//        System.exit(0);
//    }

    /**
     * In deze class bepaal je de instellingen zoals je ze voor deze tabel wilt gebruiken
     * Titels, data en welke kolom editable is
     */
    class VariabeleModel extends DefaultTableModel {

        String[] titels = {"naam", "waarde"};
        Object[][] data = util.vulVariabeleTableData();

        @Override
        public int getRowCount() {
            if (data == null) {
                return 0;
            } else {
                return data.length;
            }
        }

        @Override
        public int getColumnCount() {
            if (titels == null) {
                return 0;
            } else {
                return titels.length;
            }
        }

        /**
         * Wijzigt de standaard datavelden
         *
         * @param col
         * @return
         */
        @Override
        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /**
         * Geactiveerd aan het einde van een edit actie
         * @param value
         * @param row
         * @param col
         */
        @Override
        public void setValueAt(Object value, int row, int col) {
            if (row > data.length - 1) {
                System.out.println("probleem!!");
            } else {
                data[row][col] = value;
                fireTableCellUpdated(row, col);
            }
        }

        /**
         * Wijzigt de standaard columnnames
         *
         * @param col
         * @return
         */
        @Override
        public String getColumnName(int col) {
            return titels[col];
        }

        /**
         * Alleen de 2e kolom is editable
         *
         * @param row
         * @param col
         * @return
         */
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 1;
        }

        public void removeRow(int row) {
            System.out.println("remove row" + row);
        }

    }

}
