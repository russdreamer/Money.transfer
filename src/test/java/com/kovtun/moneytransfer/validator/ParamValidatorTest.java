package com.kovtun.moneytransfer.validator;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.currency.CurrencyConverter;
import com.kovtun.moneytransfer.dto.Account;
import com.kovtun.moneytransfer.dto.User;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

public class ParamValidatorTest {
    @Test
    public void isUserFieldsNotValidTest() {
        User user = new User("", "", "", "1234", null);
        boolean isFalse = ParamValidator.isUserFieldsNotValid(user);
        Assert.assertTrue(isFalse);
    }

    @Test
    public void isNotEnoughMoneyTest() throws IOException {
        Account account = new Account(1L, 1234567890L, 100L, Currency.RUB, 4L);
        boolean isMoneyLack = ParamValidator.isNotEnoughMoney(account, Currency.RUB, 50, new CurrencyConverter());
        Assert.assertFalse(isMoneyLack);

        isMoneyLack = ParamValidator.isNotEnoughMoney(account, Currency.RUB, 500, new CurrencyConverter());
        Assert.assertTrue(isMoneyLack);
    }

    @Test
    public void isNotValidLongTest() {
        boolean isFalse = ParamValidator.isNotValidLong("123", null);
        Assert.assertTrue(isFalse);

        isFalse = ParamValidator.isNotValidLong("123", "NOT_VALID_LONG");
        Assert.assertTrue(isFalse);

        isFalse = ParamValidator.isNotValidLong("123", "456788");
        Assert.assertFalse(isFalse);
    }

    @Test
    public void isUserUnderageTest() {
        User user = new User("Ivan", "Ivanov", "IVanovich", "1234", Date.valueOf(LocalDate.now()));
        boolean isUnderage = ParamValidator.isUserUnderage(user);
        Assert.assertTrue(isUnderage);

        user = new User("Ivan", "Ivanov", "Ivanovich", "1234", Date.valueOf("1980-02-04"));
        isUnderage = ParamValidator.isUserUnderage(user);
        Assert.assertFalse(isUnderage);
    }

    @Test
    public void isNullTest() {
        Assert.assertTrue(ParamValidator.isNull(null, null));
        Assert.assertTrue(ParamValidator.isNull("1234", null));
        Assert.assertTrue(ParamValidator.isNull(12345, null));

        Assert.assertFalse(ParamValidator.isNull(12345, "12345"));
        Assert.assertFalse(ParamValidator.isNull("12345", 12345));
    }
}
