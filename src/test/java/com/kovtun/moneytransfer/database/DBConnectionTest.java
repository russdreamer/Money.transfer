package com.kovtun.moneytransfer.database;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionTest {
    @Test
    public void getConnectionTest() throws SQLException {
        Connection connection = DBConnection.getConnection();
        Assert.assertNotNull(connection);
    }
}
