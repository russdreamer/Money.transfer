package com.kovtun.moneytransfer.dto;

public class User {
    private long id;
    private String firstName;
    private String secondName;
    private String patronymicName;
    private String passportNum;
    private String birthdate;

    public User() {}

    public User(String fName, String sName, String pName, String passportNum, String birthdate) {
        this.firstName = fName;
        this.secondName = sName;
        this.patronymicName = pName;
        this.passportNum = passportNum;
        this.birthdate = birthdate;
    }

    public User(long id, String fName, String sName, String pName, String passportNum, String birthdate) {
        this.id = id;
        this.firstName = fName;
        this.secondName = sName;
        this.patronymicName = pName;
        this.passportNum = passportNum;
        this.birthdate = birthdate;
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public String getPatronymicName() {
        return patronymicName;
    }

    public String getPassportNum() {
        return passportNum;
    }

    public String getBirthdate() {
        return birthdate;
    }
}
