package com.kovtun.moneytransfer.handler;

import com.kovtun.moneytransfer.pojo.User;
import spark.Request;
import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static spark.Spark.*;

public class Controller {

    public void run() {
        handleRequest();
    }

    private void handleRequest() {
        get(CREATE_ACCOUNT, (req, res) -> createAccount(req));
        get(DELETE_ACCOUNT, (req, res) -> deleteAccount(req));
        get(TRANSFER_MONEY, (req, res) -> transferMoney(req));
        get(TOP_UP, (req, res) -> topUpAccount(req));
    }

    private String createAccount(Request req) {
        User user = getUser(req);
        String currency = req.queryParams(CURRENCY);
        return ReqHandler.createAccount(user, currency);
    }

    private String deleteAccount(Request req) {
        User user = getUser(req);
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        return ReqHandler.deleteAccount(user, accountNum);
    }

    private String transferMoney(Request req) {
        User user = getUser(req);
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        String targetAccount = req.queryParams(TARGET_ACCOUNT);
        String amount = req.queryParams(AMOUNT);
        String currency = req.queryParams(CURRENCY);
        return ReqHandler.transferMoney(user, accountNum, targetAccount, amount, currency);
    }

    private String topUpAccount(Request req) {
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        String amount = req.queryParams(AMOUNT);
        String currency = req.queryParams(CURRENCY);
        return ReqHandler.topUpAccount(accountNum, amount, currency);
    }

    private User getUser(Request req) {
        String fName = req.queryParams(FIRST_NAME);
        String sName = req.queryParams(SECOND_NAME);
        String pName = req.queryParams(PATRONYMIC_NAME);
        String passportNum = req.queryParams(PASSPORT_NUM);
        String birthdate = req.queryParams(BIRTHDATE);
        return new User(fName, sName, pName, passportNum, birthdate);
    }
}
