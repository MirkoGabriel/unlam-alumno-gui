package org.example;

import person.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
        setSize(1040, 320);
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
                        studentsTable.setModel(studentModel);
                    }else {
                        studentsTable.setModel(new DefaultTableModel());
                    }

                } else {
                    textPanel.setVisible(false);
                    sqlPanel.setVisible(true);
                    if (daoSql != null) {
                        System.out.println("take the aready instanced sql");
                        dao = daoSql;
                        studentsTable.setModel(studentModel);
                    }else{
                        studentsTable.setModel(new DefaultTableModel());
                    }
                }
                try {
                    if (dao != null)
                        students = dao.findAll(onlyActive);
                        studentModel.setStudents(students);
                        studentModel.fireTableDataChanged();
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
                        config.put(DaoFactory.SQL_URL, pathTextField.getText());
                        config.put(DaoFactory.SQL_USER, userTextField.getText());
                        config.put(DaoFactory.SQL_PASS, passTextField.getText());

                        daoSql = (StudentDaoSql) DaoFactory.getInstance().crearDao(config);
                        dao = daoSql;
                        connectButton.setEnabled(false);
                        disconnectButton.setEnabled(true);
                        students = dao.findAll(onlyActive);
                        studentModel.setStudents(students);
                        studentModel.fireTableDataChanged();
                        studentsTable.setModel(studentModel);
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
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, null, false);
                studentDialog.setVisible(true);
                StudentDTO studentDTO = studentDialog.getStudentDTO();
                System.out.println(studentDTO.getName());
                try {
                    //si hay excpetion el dialogo que se mantenga abierto
                    Student student = dtoToStudent(studentDTO);
                    dao.create(student);
                } catch (DaoException | PersonNameException | PersonDniException | StudentException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                if (selectedStudent < 0) {
                    //JOPTION PANE
                    System.out.println("No se selecciono nada");
                    return;
                }

                StudentDTO studentDTO = aluToDTO(students.get(selectedStudent));
                StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, studentDTO, false);
                studentDialog.setVisible(true);
                studentDTO = studentDialog.getStudentDTO();
                System.out.println(studentDTO);
                try {
                    //si hay excpetion el dialogo que se mantenga abierto
                    Student student = dtoToStudent(studentDTO);
                    dao.update(student);
                } catch (DaoException | PersonNameException | PersonDniException | StudentException ex) {
                    JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        getButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedStudent = studentsTable.getSelectedRow();
                if (selectedStudent < 0) {
                    //JOPTION PANE
                    System.out.println("No se selecciono nada");
                    return;
                }

                StudentDTO studentDTO = aluToDTO(students.get(selectedStudent));
                StudentDialog studentDialog = new StudentDialog(AlumnoGUI.this, true, studentDTO, true);
                studentDialog.setVisible(true);
            }
        });
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

    private Student dtoToStudent(StudentDTO studentDTO) throws PersonNameException, PersonDniException, StudentException {
        Student student = new Student();
        student.setDni(studentDTO.getDni());
        student.setName(studentDTO.getName());
        student.setSurname(studentDTO.getSurname());
        student.setBirthday(studentDTO.getBirthday());
        student.setAdmissionDate(studentDTO.getAdmissionDate());
        student.setGender(studentDTO.getGender());
        student.setApprovedSubjectQuantity(studentDTO.getApprovedSubjectQuantity());
        student.setAverage(studentDTO.getAverage());
        return student;
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
            studentsTable.setModel(studentModel);
        } catch (DaoFactoryException | DaoException ex) {
            JOptionPane.showMessageDialog(AlumnoGUI.this, ex.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new AlumnoGUI();
    }
}
