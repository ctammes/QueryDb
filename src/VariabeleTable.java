import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;

public class VariabeleTable extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTable tblVariabele;

    Utility util = new Utility();

    public VariabeleTable() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        String[] titels = { "naam", "waarde"};
        Object[][] data = util.vulVariabeleTableData();
        DefaultTableModel mod = (DefaultTableModel) tblVariabele.getModel();
        for (Object[] rij : data) {
            mod.addRow(rij);
        }

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
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
    }

    private void onOK() {
// add your code here
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        VariabeleTable dialog = new VariabeleTable();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
