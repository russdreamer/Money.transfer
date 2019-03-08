package com.kovtun.moneytransfer.handler;

import com.kovtun.moneytransfer.dao.DaoManager;
import com.kovtun.moneytransfer.dto.User;
import spark.Request;

import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.ServerConstants.DEFAULT_SERVER_PORT;
import static spark.Spark.*;

public class Controller {

    /**
     * run the server
     */
    public void run() {
        port(DEFAULT_SERVER_PORT);
        handleRequest();
    }

    /**
     * handle and process REST API requests on server
     */
    private void handleRequest() {
        get(CREATE_ACCOUNT, (req, res) -> createAccount(req));
        get(DELETE_ACCOUNT, (req, res) -> deleteAccount(req));
        get(TRANSFER_MONEY, (req, res) -> transferMoney(req));
        get(TOP_UP, (req, res) -> topUpAccount(req));
    }

    /**
     * create client's account
     * @param req received request
     * @return result response as json
     */
    private String createAccount(Request req) {
        User user = getUser(req);
        String currency = req.queryParams(CURRENCY);
        return DaoManager.createAccount(user, currency);
    }

    /**
     * delete client's account
     * @param req received request
     * @return result response as json
     */
    private String deleteAccount(Request req) {
        User user = getUser(req);
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        return DaoManager.deleteAccount(user, accountNum);
    }

    /**
     * transfer money between accounts
     * @param req received request
     * @return result response as json
     */
    private String transferMoney(Request req) {
        User user = getUser(req);
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        String targetAccount = req.queryParams(TARGET_ACCOUNT);
        String amount = req.queryParams(AMOUNT);
        String currency = req.queryParams(CURRENCY);
        return DaoManager.transferMoney(user, accountNum, targetAccount, amount, currency);
    }

    /**
     * refilling money to account
     * @param req received request
     * @return result response as json
     */
    private String topUpAccount(Request req) {
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        String amount = req.queryParams(AMOUNT);
        String currency = req.queryParams(CURRENCY);
        return DaoManager.topUpAccount(accountNum, amount, currency);
    }

    /**
     * create user instance from income request
     * @param req received request
     * @return user instance. The user might be non-existed or with null fields
     */
    private User getUser(Request req) {
        String fName = req.queryParams(FIRST_NAME);
        String sName = req.queryParams(SECOND_NAME);
        String pName = req.queryParams(PATRONYMIC_NAME);
        String passportNum = req.queryParams(PASSPORT_NUM);
        Date birthdate = getDateFromParam(req.queryParams(BIRTHDATE));
        return new User(fName, sName, pName, passportNum, birthdate);
    }

    /**
     * convert String query param to java.sql.Date
     * @param param date query param in format yyyy-MM-dd
     * @return java.sql.Date if success; <code>null</code> if param format is wrong
     */
    private Date getDateFromParam(String param) {
        try {
            return Date.valueOf(param);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
