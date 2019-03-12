package com.kovtun.moneytransfer.currency;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class CurrencyConverterTest {

    @Test
    public void convertTest() throws IOException {
        CurrencyConverter converter = new CurrencyConverter();
        Long usdAmount = converter.convert(Currency.USD, 1, Currency.RUB);
        Assert.assertNotNull(usdAmount);
    }
}
