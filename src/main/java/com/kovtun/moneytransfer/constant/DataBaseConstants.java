package com.kovtun.moneytransfer.constant;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;

public class DataBaseConstants {
    /* DataBase connect */
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";

    /* SQL requests */
    public static final String CREATE_USERS_TABLE_QUERY =  "CREATE TABLE Users " +
            "(" + ID + " IDENTITY not NULL PRIMARY KEY, " +
            FIRST_NAME + " VARCHAR(255), " +
            SECOND_NAME + " VARCHAR(255), " +
            PATRONYMIC_NAME + " VARCHAR(255), " +
            PASSPORT_NUM + " VARCHAR(255), " +
            BIRTHDATE + " DATE)";

    public static final String CREATE_ACCOUNTS_TABLE_QUERY =  "CREATE TABLE Accounts " +
            "(" + ID + " IDENTITY not NULL PRIMARY KEY, " +
            ACCOUNT_NUMBER + " BIGINT not NULL, " +
            AMOUNT + " BIGINT, " +
            CURRENCY + " VARCHAR(255), " +
            HOLDER_ID + " BIGINT, " +
            "foreign key (" + HOLDER_ID + ") references Users(" + ID + ") )";

    public static final String CREATE_USER_QUERY =  "INSERT INTO Users VALUES (NULL, ?, ?, ?, ?, ?)";
    public static final String DELETE_USER_QUERY =  "DELETE FROM Users WHERE id = ?";
    public static final String CREATE_ACCOUNT_QUERY =  "INSERT INTO Accounts VALUES (NULL, ?, ?, ?, ?)";
    public static final String DELETE_ACCOUNT_QUERY =  "DELETE FROM Accounts WHERE id = ?";
    public static final String GET_ACCOUNT_BY_ID_QUERY =  "SELECT * FROM Accounts WHERE " + ACCOUNT_NUMBER + " = ?";
    public static final String GET_USER_ACCOUNTS_QUERY =  "SELECT * FROM Accounts WHERE " + HOLDER_ID + " = ?";
    public static final String UPDATE_ACCOUNT_AMOUNT =  "UPDATE Accounts SET " + AMOUNT + " = ? WHERE id = ?";
    public static final String GET_USER_QUERY =  "SELECT * FROM Users WHERE " + FIRST_NAME + " = ?, " +
            SECOND_NAME + " = ?," + PATRONYMIC_NAME + " = ?," + PASSPORT_NUM + " = ?," + BIRTHDATE + " = ?";
}
