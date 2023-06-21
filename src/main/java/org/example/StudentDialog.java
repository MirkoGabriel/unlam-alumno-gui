package org.example;

import com.toedter.calendar.JDateChooser;
import person.*;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField dniTextField;
    private JTextField nameTextField;
    private JTextField surnameTextField;
    private JDateChooser birthday;
    private JDateChooser admissionDate;
    private JTextField approvedSubjectsTextField;
    private JTextField averageTextField;
    private JTextField genderTextField;
    private JPanel butttonPane;
    private List<Student> students = new ArrayList<>();
    public StudentDTO getStudentDTO() {
        return studentDTOs;
    }

    private StudentDTO studentDTOs;

    public StudentDialog(java.awt.Frame parent, boolean modal, StudentDTO studentDTO, boolean isGetInfo, Dao<Student,
            Integer> dao, boolean onlyActive, JTable studentsTable, StudentModel studentModel) {
        super(parent, modal);
        setContentPane(contentPane);
        setSize(360, 280);
        setLocationRelativeTo(null);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        if (studentDTO == null) {
            this.studentDTOs = new StudentDTO();
            setTitle("Create Student");
        } else {
            this.studentDTOs = studentDTO;
            dniTextField.setText(String.valueOf(studentDTO.getDni()));
            nameTextField.setText(studentDTO.getName());
            surnameTextField.setText(studentDTO.getSurname());
            birthday.setCalendar(studentDTO.getBirthday());
            admissionDate.setCalendar(studentDTO.getAdmissionDate());
            genderTextField.setText(String.valueOf(studentDTO.getGender()));
            approvedSubjectsTextField.setText(String.valueOf(studentDTO.getApprovedSubjectQuantity()));
            averageTextField.setText(String.valueOf(studentDTO.getAverage()));
            dniTextField.setEnabled(false);
            setTitle(isGetInfo ? "Info Student" : "Update Student");
            if(isGetInfo){
                butttonPane.setVisible(false);
            }
        }


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
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Student student = new Student();
                    student.setDni(Integer.valueOf(dniTextField.getText()));
                    student.setName(nameTextField.getText());
                    student.setSurname(surnameTextField.getText());
                    student.setBirthday(new MyCalendar(birthday.getCalendar()));
                    student.setAdmissionDate(new MyCalendar(admissionDate.getCalendar()));
                    student.setApprovedSubjectQuantity(Integer.valueOf(approvedSubjectsTextField.getText()));
                    student.setAverage(Double.valueOf(averageTextField.getText()));
                    student.setGender(genderTextField.getText().charAt(0));

                    if (studentDTO == null) {
                        dao.create(student);
                    } else {
                        dao.update(student);
                    }
                    setVisible(false);
                } catch (DaoException | PersonNameException | PersonDniException | StudentException ex) {
                    JOptionPane.showMessageDialog(StudentDialog.this,
                            ex.getLocalizedMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
    }




    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        StudentDialog dialog = new StudentDialog(new javax.swing.JFrame(), true, null, false,
                null, false, null, null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        birthday = new JDateChooser();
        admissionDate =new JDateChooser();
    }
}
