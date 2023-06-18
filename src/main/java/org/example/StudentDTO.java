package org.example;

import person.MyCalendar;

public class StudentDTO {
    private int dni;
    private String name;
    private String surname;
    private MyCalendar birthday;
    private MyCalendar admissionDate;

    public MyCalendar getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(MyCalendar admissionDate) {
        this.admissionDate = admissionDate;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public MyCalendar getBirthday() {
        return birthday;
    }

    public void setBirthday(MyCalendar birthday) {
        this.birthday = birthday;
    }
}
