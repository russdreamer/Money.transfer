package com.kovtun.moneytransfer.constant;

public class DataBaseConstants {
    /* DataBase connect */
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";

    /* SQL requests */
    public static final String CREATE_USERS_TABLE =  "CREATE TABLE Users " +
            "(id IDENTITY not NULL PRIMARY KEY, " +
            " firstName VARCHAR(255), " +
            " secondName VARCHAR(255), " +
            " patronymicName VARCHAR(255), " +
            " passportNum VARCHAR(255), " +
            " birthdate DATE)";

    public static final String CREATE_ACCOUNTS_TABLE =  "CREATE TABLE Accounts " +
            "(id IDENTITY not NULL PRIMARY KEY, " +
            " account BIGINT, " +
            " balance BIGINT, " +
            " currency VARCHAR(255), " +
            "userKey BIGINT, " +
            "foreign key (userKey) references Users(id) )";
}
