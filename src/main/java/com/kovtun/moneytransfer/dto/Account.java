package com.kovtun.moneytransfer.dto;

public class Account {
    private long id;
    private long account;
    private long balance;
    private String currency;

    public Account() {}

    public Account(long account, long balance, String currency) {
        this.account = account;
        this.balance = balance;
        this.currency = currency;
    }

    public Account(long id, long account, long balance, String currency) {
        this.id = id;
        this.account = account;
        this.balance = balance;
        this.currency = currency;
    }

    public long getId() {
        return id;
    }

    public long getAccount() {
        return account;
    }

    public long getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}
