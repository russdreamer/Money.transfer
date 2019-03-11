package com.kovtun.moneytransfer.request;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dto.User;

public class MoneyTransferRequest {
    private User user;
    private long accountNum;
    private long targetAccountNum;
    private long amount;
    private Currency currency;

    public MoneyTransferRequest(User user, long accountNum, long targetAccountNum, long amount, Currency currency) {
        this.user = user;
        this.accountNum = accountNum;
        this.targetAccountNum = targetAccountNum;
        this.amount = amount;
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public long getAccountNum() {
        return accountNum;
    }

    public long getTargetAccountNum() {
        return targetAccountNum;
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
