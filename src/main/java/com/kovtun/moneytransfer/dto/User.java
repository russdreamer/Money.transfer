package com.kovtun.moneytransfer.dto;

import java.sql.Date;

public class User {
    private long id;
    private String firstName;
    private String secondName;
    private String patronymicName;
    private String passportNum;
    private Date birthdate;

    public User() {}

    public User(String fName, String sName, String pName, String passportNum, Date birthdate) {
        this.firstName = fName;
        this.secondName = sName;
        this.patronymicName = pName;
        this.passportNum = passportNum;
        this.birthdate = birthdate;
    }

    public User(long id, String fName, String sName, String pName, String passportNum, Date birthdate) {
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

    public Date getBirthdate() {
        return birthdate;
    }
}
