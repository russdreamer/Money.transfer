package com.kovtun.moneytransfer;

import com.kovtun.moneytransfer.database.DBTables;
import com.kovtun.moneytransfer.handler.Controller;

public class AppLauncher {

    public static void main(String[] args) {
        if (dataBaseInit())
            runServer();
    }

    /**
     * initialization database with creation necessary entities
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    private static boolean dataBaseInit() {
         return DBTables.createTables();
    }

    /**
     * run REST server
     */
    private static void runServer() {
        new Controller().run();
    }
}
