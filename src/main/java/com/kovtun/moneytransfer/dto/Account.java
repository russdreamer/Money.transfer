package com.kovtun.moneytransfer.dto;

import com.kovtun.moneytransfer.currency.Currency;

public class Account {
    private Long id;
    private Long account;
    private Long amount;
    private Currency currency;
    private Long holderId;

    public Account() {}

    public Account(Long account, Long amount, Currency currency, Long holderId) {
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.holderId = holderId;
    }

    public Account(Long id, Long account, Long amount, Currency currency, Long holderId) {
        this.id = id;
        this.account = account;
        this.amount = amount;
        this.currency = currency;
        this.holderId = holderId;
    }

    public Long getId() {
        return id;
    }

    public Long getAccount() {
        return account;
    }

    public Long getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Long getHolderId() {
        return holderId;
    }
}
