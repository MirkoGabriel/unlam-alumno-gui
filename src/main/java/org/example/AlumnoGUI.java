package org.example;

import person.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
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
    private JTextField pathTextField;
    private JTextField userTextField;
    private JTextField passTextField;
    private JButton connectButton;
    private JPanel sqlPanel;
    private JButton disconnectButton;
    private Boolean onlyActive = false;
    private List<Student> students = new ArrayList<>();

    //Solo abra txt
    public AlumnoGUI() {
        setContentPane(mainPanel);
        setTitle("Alumno GUI");
        setSize(800, 320);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);
        textPanel.setVisible(true);
        sqlPanel.setVisible(false);
        disableButtons();
        disconnectButton.setEnabled(false);
        studentModel = new StudentModel();
        studentsTable.setModel(studentModel);
        studentModel.setStudents(students);

        mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
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
                if (daoTxt != null) {
                    try {
                        refreshTable();
                    } catch (DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        daoKindComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoKindComboBox.getSelectedIndex() == 0) {
                    onlyActiveCheckBox.setVisible(true);
                    textPanel.setVisible(true);
                    sqlPanel.setVisible(false);
                    if (daoTxt != null) {
                        dao = daoTxt;
                        studentsTable.setModel(studentModel);
                    } else {
                        studentsTable.setModel(new DefaultTableModel());
                    }

                } else {
                    onlyActiveCheckBox.setVisible(false);
                    textPanel.setVisible(false);
                    sqlPanel.setVisible(true);
                    if (daoSql != null) {
                        dao = daoSql;
                        studentsTable.setModel(studentModel);
                    } else {
                        studentsTable.setModel(new DefaultTableModel());
                    }
                }
                try {
                    if (dao != null && daoSql != null && daoTxt != null)
                        students = dao.findAll(onlyActive);
                    studentModel.setStudents(students);
                    studentModel.fireTableDataChanged();
                } catch (DaoException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
                        config.put(DaoFactory.SQL_URL, pathTextField.getText());
                        config.put(DaoFactory.SQL_USER, userTextField.getText());
                        config.put(DaoFactory.SQL_PASS, passTextField.getText());

                        daoSql = (StudentDaoSql) DaoFactory.getInstance().crearDao(config);
                        dao = daoSql;
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                        refreshTable();
                        ableButtons();
                    } catch (DaoFactoryException | DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (daoSql != null) {
                    try {
                        disableButtons();
                        daoSql.close();
                        daoSql = null;
                        connectButton.setEnabled(true);
                        disconnectButton.setEnabled(false);
                        studentsTable.setModel(new DefaultTableModel());
                        passTextField.setText("");
                        pathTextField.setText("");
                        userTextField.setText("");
                    } catch (DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                if (selectedStudent < 0) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, "Select a row", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (students.get(selectedStudent).isActive() == false) {
                    students.get(selectedStudent).setActive(true);
                    try {
                        dao.update(students.get(selectedStudent));
                        return;
                    } catch (DaoException ex) {
                        JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                int resp = JOptionPane.showConfirmDialog(AlumnoGUI.this,
                        "Are you sure to delete student " + students.get(selectedStudent).getName() + "?",
                        "Delete", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (resp != JOptionPane.OK_OPTION) {
                    return;
                }
                try {
                    Student student = students.get(selectedStudent);
                    dao.delete(student.getDni());
                    refreshTable();
                } catch (DaoException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, null,
                        false, dao, onlyActive, studentsTable, studentModel);
                studentDialog.setVisible(true);
                try {
                    refreshTable();
                } catch (DaoException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                if (selectedStudent < 0) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, "Select a row", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StudentDTO studentDTO = aluToDTO(students.get(selectedStudent));
                StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, studentDTO,
                        false, dao, onlyActive, studentsTable, studentModel);
                studentDialog.setVisible(true);
                try {
                    refreshTable();
                } catch (DaoException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                getInfoStudent(selectedStudent);
            }
        });

        studentsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedStudent = studentsTable.getSelectedRow();
                if (students.get(selectedStudent).isActive() == false) {
                    deleteButton.setLabel("Able");
                } else {
                    deleteButton.setLabel("Delete");
                }
                if (e.getClickCount() == 2) {
                    getInfoStudent(selectedStudent);
                }
            }
        });
    }

    public void getInfoStudent(int selectedStudent) {
        if (selectedStudent < 0) {
            JOptionPane.showMessageDialog(AlumnoGUI.this, "Select a row", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StudentDTO studentDTO = aluToDTO(students.get(selectedStudent));
        StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, studentDTO,
                true, dao, onlyActive, studentsTable, studentModel);
        studentDialog.setVisible(true);
    }

    private StudentDTO aluToDTO(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setName(student.getName());
        studentDTO.setSurname(student.getSurname());
        studentDTO.setDni(student.getDni());
        studentDTO.setBirthday(student.getBirthday());
        studentDTO.setAdmissionDate(student.getAdmissionDate());
        studentDTO.setGender(student.getGender());
        studentDTO.setApprovedSubjectQuantity(student.getApprovedSubjectQuantity());
        studentDTO.setAverage(student.getAverage());
        return studentDTO;
    }

    public void refreshTable() throws DaoException {
        students = dao.findAll(onlyActive);
        studentModel.setStudents(students);
        studentModel.fireTableDataChanged();
        studentsTable.setModel(studentModel);
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
            refreshTable();
            ableButtons();
        } catch (DaoFactoryException | DaoException ex) {
            JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void ableButtons() {
        createButton.setEnabled(true);
        updateButton.setEnabled(true);
        getButton.setEnabled(true);
        deleteButton.setEnabled(true);
    }

    public void disableButtons() {
        createButton.setEnabled(false);
        updateButton.setEnabled(false);
        getButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        new AlumnoGUI();
    }
}
