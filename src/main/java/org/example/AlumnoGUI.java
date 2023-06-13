package org.example;

import person.Student;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlumnoGUI extends JFrame {
    private Dao<Student, Integer> dao;
    private StudentDaoTxt daoTxt;
    private StudentDaoSql daoSql;
    private JButton button1;
    private JButton button2;
    private JTable table1;
    private JButton button3;
    private JButton button4;
    private JPanel mainPanel;
    private JComboBox daoKindComboBox;
    private JTextField fileSelectedTextField;
    private JButton fileBrowserButton;
    private JCheckBox onlyActiveCheckBox;
    private JPanel informationPanel;
    private JPanel textPanel;
    private JTextField textField2;
    private JTextField textField3;
    private JTextField textField4;
    private JButton connectButton;
    private JPanel sqlPanel;
    private Boolean onlyActive = false;

    //Solo abra txt
    public AlumnoGUI() {
        setContentPane(mainPanel);
        setTitle("Alumno GUI");
        setSize(520, 320);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        textPanel.setVisible(true);
        sqlPanel.setVisible(false);
        fileBrowserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readFile();
            }
        });
        onlyActiveCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onlyActive = !onlyActive;
            }
        });
        daoKindComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoKindComboBox.getSelectedIndex() == 0) {
                    textPanel.setVisible(true);
                    sqlPanel.setVisible(false);
                    if (daoTxt == null) {
                        readFile();
                    }
                    dao = daoTxt;
                    try {
                        dao.findAll(onlyActive).forEach(System.out::println);
                    } catch (DaoException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    textPanel.setVisible(false);
                    sqlPanel.setVisible(true);
                    if (daoSql != null) {
                        dao = daoSql;
                        try {
                            dao.findAll(onlyActive).forEach(System.out::println);
                        } catch (DaoException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoSql == null) {
                    try {
                        Map<String, String> config = new HashMap<>();
                        config.put(DaoFactory.TYPE_DAO, DaoFactory.DAO_SQL);
                        config.put(DaoFactory.SQL_URL, "jdbc:mysql://127.0.0.1:3306/universidad_caba");
                        config.put(DaoFactory.SQL_USER, "root");
                        config.put(DaoFactory.SQL_PASS, "root");

                        daoSql = (StudentDaoSql) DaoFactory.getInstance().crearDao(config);
                        dao = daoSql;
                        dao.findAll(onlyActive).forEach(System.out::println);
                    } catch (DaoFactoryException | DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    public void readFile() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(AlumnoGUI.this);

        if (res != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        fileSelectedTextField.setText(file.getAbsolutePath());
        try {
            Map<String, String> config = new HashMap<>();
            config.put(DaoFactory.TYPE_DAO, DaoFactory.DAO_TXT);
            config.put(DaoFactory.DAO_PATH_TXT, fileSelectedTextField.getText());
            daoTxt = (StudentDaoTxt) DaoFactory.getInstance().crearDao(config);
            dao = daoTxt;
            dao.findAll(onlyActive).forEach(System.out::println);
        } catch (DaoFactoryException | DaoException ex) {
            JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        new AlumnoGUI();
    }
}
