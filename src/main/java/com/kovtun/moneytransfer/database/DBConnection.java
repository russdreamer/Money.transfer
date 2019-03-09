package com.kovtun.moneytransfer.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.kovtun.moneytransfer.constant.DataBaseConstants.*;

public class DBConnection {

    /**
     * get a connection instance to the database
     * @return new connection instance or or <code>null</code> if failed
     */
    public static Connection getConnection() throws SQLException {
        Connection dbConnection;

        try {
            Class.forName(DB_DRIVER);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        dbConnection = DriverManager.getConnection(DB_URL);
        return dbConnection;
    }
}
