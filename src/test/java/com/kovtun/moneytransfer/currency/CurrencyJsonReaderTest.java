package com.kovtun.moneytransfer.currency;

import org.junit.Test;

import java.io.IOException;

public class CurrencyJsonReaderTest {

    @Test
    public void getCurrencyRatesTest() throws IOException {
        /* check if there is no exception */
        CurrencyJsonReader reader = new CurrencyJsonReader();
        reader.getEurRate();
        reader.getEurRate();
        reader.getEurRate();
    }
}
