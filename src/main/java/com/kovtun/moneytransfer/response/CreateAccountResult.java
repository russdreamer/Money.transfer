package com.kovtun.moneytransfer.response;

import com.kovtun.moneytransfer.currency.Currency;

public class CreateAccountResult implements Result {
    private long accountNumber;
    private long balance;
    private Currency currency;

    public CreateAccountResult(long accountNumber, long balance, Currency currency) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.currency = currency;
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    public long getBalance() {
        return balance;
    }

    public Currency getCurrency() {
        return currency;
    }
}
