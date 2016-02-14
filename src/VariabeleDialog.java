import javax.swing.*;
import java.awt.event.*;

public class VariabeleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField txtNaam;
    private JTextField txtWaarde;

    public VariabeleDialog() {
        setContentPane(contentPane);
        setModal(true);
        setVisible(false);
        getRootPane().setDefaultButton(buttonOK);
        pack();

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
        System.out.println("Nieuwe waarde: " + txtWaarde.getText());
        dispose();
    }

    private void onCancel() {
// add your code here if necessary
        dispose();
    }

    protected void setTxtNaam(String naam) {
        txtNaam.setText(naam);
    }

    protected void setTxtWaarde(String waarde) {
        txtWaarde.setText(waarde);
    }

    protected String getTxtWaarde() {
        return txtWaarde.getText();
    }

}
