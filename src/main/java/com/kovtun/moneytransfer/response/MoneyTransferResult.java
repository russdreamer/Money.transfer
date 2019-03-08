package com.kovtun.moneytransfer.response;

import com.kovtun.moneytransfer.currency.Currency;

public class MoneyTransferResult implements Result{
    private long clientAccountNumber;
    private long moneyAmount;
    private Currency currency;

    public MoneyTransferResult(long clientAccountNumber, long moneyAmount, Currency currency) {
        this.clientAccountNumber = clientAccountNumber;
        this.moneyAmount = moneyAmount;
        this.currency = currency;
    }

    public long getClientAccountNumber() {
        return clientAccountNumber;
    }

    public long getMoneyAmount() {
        return moneyAmount;
    }

    public Currency getCurrency() {
        return currency;
    }
}
