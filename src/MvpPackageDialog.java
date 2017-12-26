import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.event.*;

public class MvpPackageDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JTextField txtName;
    private JLabel txtError;
    private JCheckBox chkKotlin;
    private OnListner listner;

    public MvpPackageDialog(OnListner listner) {
        this.listner=listner;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

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
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
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
        String name=txtName.getText();
        if (TextUtils.isEmpty(name)){
            txtError.setText("Name cannot be empty");
            return;
        }
        boolean isFragment=checkBox1.isSelected();
        boolean makeInterator=checkBox2.isSelected();
        boolean isKotlin=chkKotlin.isSelected();
        listner.OnSuccess(name,isKotlin,isFragment,makeInterator);
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void create(OnListner listner) {
        MvpPackageDialog dialog = new MvpPackageDialog(listner);
        dialog.pack();
        dialog.setVisible(true);
    }
}
