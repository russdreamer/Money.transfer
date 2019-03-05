package com.kovtun.moneytransfer;


import com.kovtun.moneytransfer.handler.Controller;

public class AppLauncher {
    public static void main(String[] args) {
        dataBaseInit();
        runServer();
    }

    private static void dataBaseInit() {

    }

    private static void runServer() {
        new Controller().run();
    }
}
