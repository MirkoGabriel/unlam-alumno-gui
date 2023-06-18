package org.example;

import com.toedter.calendar.JDateChooser;
import person.MyCalendar;

import javax.swing.*;
import java.awt.event.*;

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

    public StudentDTO getStudentDTO() {
        return studentDTOs;
    }

    private StudentDTO studentDTOs;

    public StudentDialog(java.awt.Frame parent, boolean modal, StudentDTO studentDTO, boolean isGetInfo) {
        super(parent, modal);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        setLocationRelativeTo(null);

        setSize(360, 280);

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
            setTitle(isGetInfo ? "Info Student" : "Update Student");
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

                studentDTOs.setDni(Integer.valueOf(dniTextField.getText()));
                studentDTOs.setName(nameTextField.getText());
                studentDTOs.setSurname(surnameTextField.getText());
                studentDTOs.setBirthday(new MyCalendar(birthday.getCalendar()));
                studentDTOs.setAdmissionDate(new MyCalendar(admissionDate.getCalendar()));
                studentDTOs.setApprovedSubjectQuantity(Integer.valueOf(approvedSubjectsTextField.getText()));
                studentDTOs.setAverage(Double.valueOf(averageTextField.getText()));
                studentDTOs.setGender(genderTextField.getText().charAt(0));
                setVisible(false);
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
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
        StudentDialog dialog = new StudentDialog(new javax.swing.JFrame(), true, null, false);
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
