package com.kovtun.moneytransfer.request;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dto.User;

public class CreateAccountRequest {
    private User user;
    private Currency currency;

    public CreateAccountRequest(User user, Currency currency) {
        this.user = user;
        this.currency = currency;
    }

    public User getUser() {
        return user;
    }

    public Currency getCurrency() {
        return currency;
    }
}
