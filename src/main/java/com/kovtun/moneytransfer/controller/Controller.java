package com.kovtun.moneytransfer.controller;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dao.DaoManager;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.response.RespStatus;
import com.kovtun.moneytransfer.response.Response;
import spark.Request;

import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.RespConstants.WRONG_PARAMS;
import static com.kovtun.moneytransfer.constant.ServerConstants.DEFAULT_SERVER_PORT;
import static com.kovtun.moneytransfer.validator.ParamValidator.*;
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
        post(CREATE_ACCOUNT, (req, res) -> createAccount(req));
        delete(DELETE_ACCOUNT, (req, res) -> deleteAccount(req));
        get(GET_ACCOUNTS, (req, res) -> getAccounts(req));
        put(TRANSFER_MONEY, (req, res) -> transferMoney(req));
        put(TOP_UP, (req, res) -> topUpAccount(req));
    }

    /**
     * get all client's accounts
     * @param req received request
     * @return result response as json
     */
    private String getAccounts(Request req) {
        User user = getUser(req);
        if (isUserFieldsNotValid(user))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        return DaoManager.getAccounts(user);
    }

    /**
     * create client's account
     * @param req received request
     * @return result response as json
     */
    private String createAccount(Request req) {
        User user = getUser(req);
        String currency = req.queryParams(CURRENCY);
        if (isUserFieldsNotValid(user) || isCurrencyNotValid(currency))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        return DaoManager.createAccount(user, Currency.valueOf(currency));
    }

    /**
     * delete client's account
     * @param req received request
     * @return result response as json
     */
    private String deleteAccount(Request req) {
        User user = getUser(req);
        String accountNum = req.queryParams(ACCOUNT_NUMBER);
        if (isUserFieldsNotValid(user) || isNotValidLong(accountNum))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        return DaoManager.deleteAccount(user, Long.valueOf(accountNum));
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
        if (isUserFieldsNotValid(user) || isNotValidLong(accountNum, targetAccount, amount) || isCurrencyNotValid(currency))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        return DaoManager.transferMoney(user, Long.valueOf(accountNum), Long.valueOf(targetAccount), Long.valueOf(amount), Currency.valueOf(currency));
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
        if ( isNotValidLong(accountNum, amount) || isCurrencyNotValid(currency))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        return DaoManager.topUpAccount(Long.valueOf(accountNum), Long.valueOf(amount), Currency.valueOf(currency));
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
            return null;
        }
    }
}
