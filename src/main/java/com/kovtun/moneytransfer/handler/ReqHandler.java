package com.kovtun.moneytransfer.handler;

import com.kovtun.moneytransfer.dto.User;

class ReqHandler {
    /**
     * create new bank account. If user is new, it will also create the user
     * @param user client whose account is going to be created
     * @param accountCurrency client's account currency
     * @return result response as json
     */
    static String createAccount(User user, String accountCurrency) {
        return null;
    }

    /**
     * delete bank account. If it's the last user's account, it will also delete the user
     * @param user client whose account is going to be deleted
     * @return result response as json
     */
    static String deleteAccount(User user, String userAccount) {
        return null;
    }

    /**
     * transfer money between accounts of the only user
     * @param user client whose money are being moved between his accounts
     * @param userAccount client account to be charged
     * @param targetAccount account to be refilled
     * @param amount amount of money
     * @param currency currency in which the transaction is performed
     * @return result response as json
     */
    static String transferMoney(User user, String userAccount, String targetAccount, String amount, String currency) {
        return null;
    }

    /**
     * refilling account balance
     * @param accountNum client account to be refilled
     * @param amount amount of money
     * @param currency currency in which the transaction is performed
     * @return result response as json
     */
    static String topUpAccount(String accountNum, String amount, String currency) {
        return null;
    }
}
