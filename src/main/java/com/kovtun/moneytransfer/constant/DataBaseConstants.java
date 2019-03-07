package com.kovtun.moneytransfer.constant;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;

public class DataBaseConstants {
    /* DataBase connect */
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false";

    /* SQL requests */
    public static final String CREATE_USERS_TABLE =  "CREATE TABLE Users " +
            "(id IDENTITY not NULL PRIMARY KEY, " +
            FIRST_NAME + " VARCHAR(255), " +
            SECOND_NAME + " VARCHAR(255), " +
            PATRONYMIC_NAME + " VARCHAR(255), " +
            PASSPORT_NUM + " VARCHAR(255), " +
            BIRTHDATE + " DATE)";

    public static final String CREATE_ACCOUNTS_TABLE =  "CREATE TABLE Accounts " +
            "(id IDENTITY not NULL PRIMARY KEY, " +
            ACCOUNT_NUMBER + " BIGINT, " +
            AMOUNT + " BIGINT, " +
            CURRENCY + " VARCHAR(255), " +
            HOLDER_ID + " BIGINT, " +
            "foreign key (" + HOLDER_ID + ") references Users(" + ID + ") )";

    public static final String CREATE_USER =  "INSERT INTO Users VALUES (NULL, ?, ?, ?, ?, ?)";
    public static final String DELETE_USER =  "DELETE FROM Users WHERE id = ?";
    public static final String GET_USER_BY_ID =  "SELECT * FROM Users WHERE id = ?";
}
