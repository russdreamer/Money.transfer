package com.kovtun.moneytransfer.dto;

import com.kovtun.moneytransfer.currency.Currency;

public class Account {
    private long id;
    private long account;
    private long amount;
    private Currency currency;
    private long holderId;

    public Account() {}

    public Account(long account, long amount, Currency currency, long holderId) {
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.holderId = holderId;
    }

    public Account(long id, long account, long amount, Currency currency, long holderId) {
        this.id = id;
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.holderId = holderId;
    }

    public long getId() {
        return id;
    }

    public long getAccount() {
        return account;
    }

    public long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public long getHolderId() {
        return holderId;
    }
}
