package com.kovtun.moneytransfer.response;

import com.kovtun.moneytransfer.currency.Currency;

public class DeleteAccountResult implements Result{
    private long removedAccount;
    private long availableFunds;
    private Currency currency;
    private boolean hasOtherAccounts;

    public DeleteAccountResult(long removedAccount, long availableFunds, Currency currency, boolean hasOtherAccounts) {
        this.removedAccount = removedAccount;
        this.availableFunds = availableFunds;
        this.currency = currency;
        this.hasOtherAccounts = hasOtherAccounts;
    }

    public long getRemovedAccount() {
        return removedAccount;
    }

    public long getAvailableFunds() {
        return availableFunds;
    }

    public Currency getCurrency() {
        return currency;
    }

    public boolean isHasOtherAccounts() {
        return hasOtherAccounts;
    }
}
