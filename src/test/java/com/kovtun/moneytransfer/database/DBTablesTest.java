package com.kovtun.moneytransfer.database;

import org.junit.Assert;
import org.junit.Test;

public class DBTablesTest {
    @Test
    public void createTablesTest(){
        boolean isCreated = DBTables.createTables();
        Assert.assertTrue(isCreated);
    }
}
