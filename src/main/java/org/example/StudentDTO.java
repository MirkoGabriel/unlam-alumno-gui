package org.example;

import person.MyCalendar;

public class StudentDTO {
    private int dni;
    private String name;
    private String surname;
    private MyCalendar birthday;
    private MyCalendar admissionDate;
    private char gender;
    private int approvedSubjectQuantity;
    private double average;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    private boolean active;

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public int getApprovedSubjectQuantity() {
        return approvedSubjectQuantity;
    }

    public void setApprovedSubjectQuantity(int approvedSubjectQuantity) {
        this.approvedSubjectQuantity = approvedSubjectQuantity;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

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
