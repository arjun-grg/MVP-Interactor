package com.wongel.MVPGenerator;

import org.apache.http.util.TextUtils;

import javax.swing.*;
import java.awt.*;
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
            txtError.setText("Package Name cannot be empty");
            return;
        }
        boolean isFragment=checkBox1.isSelected();
        boolean makeInterator=checkBox2.isSelected();
        boolean isKotlin=chkKotlin.isSelected();
        listner.OnSuccess(name,isKotlin,isFragment,makeInterator);
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void create(OnListner listner) {
        MvpPackageDialog dialog = new MvpPackageDialog(listner);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.pack();
        dialog.setVisible(true);
    }
}
