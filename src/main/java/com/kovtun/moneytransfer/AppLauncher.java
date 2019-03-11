package com.kovtun.moneytransfer;

import com.kovtun.moneytransfer.database.DBTables;
import com.kovtun.moneytransfer.controller.Controller;

public class AppLauncher {

    public static void main(String[] args) {
        if (DBTables.createTables())
            new Controller().run();
    }
}
