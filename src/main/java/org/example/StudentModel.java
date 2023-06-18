package org.example;

import person.Student;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentModel extends AbstractTableModel {
    private static final int DNI = 0;
    private static final int NAME = 1;
    private static final int SURNAME = 2;
    private static final int BIRTHDAY = 3;
    private static final String[] HEADERS = {"Dni", "Name", "Surnam", "Birthday"};
    private List<Student> students;

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public StudentModel() {
        students = new ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return students.size();
    }

    @Override
    public int getColumnCount() {
        return HEADERS.length;
    }

    @Override
    public String getColumnName(int column) {
        return HEADERS[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student student = students.get(rowIndex);
        switch (columnIndex) {
            case DNI:
                return student.getDni();
            case NAME:
                return student.getName();
            case SURNAME:
                return student.getSurname();
            case BIRTHDAY:
                return student.getBirthday();
            default:
                return null;
        }
    }
}
