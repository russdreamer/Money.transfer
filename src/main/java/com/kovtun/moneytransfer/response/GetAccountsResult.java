package com.kovtun.moneytransfer.response;

import com.kovtun.moneytransfer.dto.Account;

import java.util.List;

public class GetAccountsResult implements Result {
    private List<Account> clientAccounts;

    public GetAccountsResult(List<Account> clientAccounts) {
        this.clientAccounts = clientAccounts;
    }

    public List<Account> getClientAccounts() {
        return clientAccounts;
    }
}
