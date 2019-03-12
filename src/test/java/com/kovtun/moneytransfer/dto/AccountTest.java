package com.kovtun.moneytransfer.dto;

import com.kovtun.moneytransfer.currency.Currency;
import org.junit.Assert;
import org.junit.Test;

public class AccountTest {
    @Test
    public void dtoGettersTest(){
        Long id = 1L;
        Long accountNum = 1234567890L;
        Long amount = 100L;
        Currency currency = Currency.EUR;
        Long holderId = 4L;
        Account account = new Account(id,accountNum, amount, currency, holderId);

        Assert.assertEquals(account.getId(), id);
        Assert.assertEquals(account.getAccount(), accountNum);
        Assert.assertEquals(account.getAmount(), amount);
        Assert.assertSame(account.getCurrency(), currency);
        Assert.assertEquals(account.getHolderId(), holderId);
    }
}
