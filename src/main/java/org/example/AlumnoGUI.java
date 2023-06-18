package org.example;

import person.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlumnoGUI extends JFrame {
    private StudentModel studentModel;
    private Dao<Student, Integer> dao;
    private StudentDaoTxt daoTxt;
    private StudentDaoSql daoSql;
    private JButton createButton;
    private JButton deleteButton;
    private JTable studentsTable;
    private JButton updateButton;
    private JButton getButton;
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
    private JButton disconnectButton;
    private Boolean onlyActive = false;
    private List<Student> students = new ArrayList<>();

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
        deleteButton.setEnabled(false);
        disconnectButton.setEnabled(false);
        studentModel = new StudentModel();
        studentsTable.setModel(studentModel);
        studentModel.setStudents(students);

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
                    if (daoTxt != null) {
                        System.out.println("Mirko");
                        dao = daoTxt;
                    }

                } else {
                    textPanel.setVisible(false);
                    sqlPanel.setVisible(true);
                    if (daoSql != null) {
                        System.out.println("take the aready instanced sql");
                        dao = daoSql;
                    }
                }
                try {
                    if (dao != null)
                        dao.findAll(onlyActive).forEach(System.out::println);
                } catch (DaoException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoSql == null) {
                    try {
                        System.out.println("First time connection");
                        Map<String, String> config = new HashMap<>();
                        config.put(DaoFactory.TYPE_DAO, DaoFactory.DAO_SQL);
                        config.put(DaoFactory.SQL_URL, "jdbc:mysql://127.0.0.1:3306/universidad_caba");
                        config.put(DaoFactory.SQL_USER, "root");
                        config.put(DaoFactory.SQL_PASS, "root");

                        daoSql = (StudentDaoSql) DaoFactory.getInstance().crearDao(config);
                        dao = daoSql;
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                        students = dao.findAll(onlyActive);
                        studentModel.setStudents(students);
                        studentModel.fireTableDataChanged();
                        deleteButton.setEnabled(true);
                    } catch (DaoFactoryException | DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("idk");
                }
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoSql != null) {
                    try {
                        System.out.println("disconnect");
                        daoSql.close();
                        daoSql = null;
                        connectButton.setEnabled(true);
                        disconnectButton.setEnabled(false);
                    } catch (DaoException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                if (selectedStudent < 0) {
                    //JOPTION PANE
                    System.out.println("No se selecciono nada");
                    return;
                }
                int resp = JOptionPane.showConfirmDialog(AlumnoGUI.this, "Are you sure to delete?",
                        "Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resp != JOptionPane.OK_OPTION) {
                    return;
                }
                try {
                    Student student = students.get(selectedStudent);
                    dao.delete(student.getDni());
                    System.out.println(student.getDni());
                } catch (DaoException ex) {
                    throw new RuntimeException(ex);
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
            students = dao.findAll(onlyActive);
            studentModel.setStudents(students);
            studentModel.fireTableDataChanged();
        } catch (DaoFactoryException | DaoException ex) {
            JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AlumnoGUI();
    }
}
