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
    private JCheckBox chkKotlin;
    private JTextField txtName;
    private JLabel txtError;
    private JRadioButton rdbNone;
    private JRadioButton rdbMosby1;
    private JRadioButton rdbMosby3;
    private ButtonGroup group = new ButtonGroup();
    private OnDialogListner listner;

    public MvpPackageDialog(OnDialogListner listner) {
        this.listner=listner;
        group.add(rdbNone);
        group.add(rdbMosby1);
        group.add(rdbMosby3);
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

    public static MvpPackageDialog create(OnDialogListner listner) {
        MvpPackageDialog dialog = new MvpPackageDialog(listner);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - dialog.getWidth()) / 2;
        final int y = (screenSize.height - dialog.getHeight()) / 2;
        dialog.setLocation(x, y);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    private void onCancel() {
        dispose();
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
        MvpModule.MVP_TYPE mosbyType = MvpModule.MVP_TYPE.None;

        if (rdbMosby1.isSelected())
            mosbyType = MvpModule.MVP_TYPE.mosby;
        else if (rdbMosby3.isSelected())
            mosbyType = MvpModule.MVP_TYPE.mosby3;

        listner.OnSuccess(this, new MvpModule(name, isFragment, makeInterator, isKotlin, mosbyType));
    }

    public void setError(String msg) {
        txtError.setText(msg);
    }
}
