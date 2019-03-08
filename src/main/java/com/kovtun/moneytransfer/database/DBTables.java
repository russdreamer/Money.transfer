package com.kovtun.moneytransfer.database;

import java.sql.*;

import static com.kovtun.moneytransfer.constant.DataBaseConstants.CREATE_ACCOUNTS_TABLE_QUERY;
import static com.kovtun.moneytransfer.constant.DataBaseConstants.CREATE_USERS_TABLE_QUERY;

public class DBTables {
    /**
     * create all the database tables that are necessary for application
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    public static boolean createTables(){
        return
                createTable(CREATE_USERS_TABLE_QUERY) &&
                createTable(CREATE_ACCOUNTS_TABLE_QUERY);
    }

    /**
     * create a database table
     * @param sql request with table creation
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    private static boolean createTable(String sql){
        try (Connection connection = DBConnection.getConnection();
             Statement statement = connection != null ? connection.createStatement() : null){

            if (statement == null)
                return false;

            statement.execute(sql);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
