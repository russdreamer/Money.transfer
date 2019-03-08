package com.kovtun.moneytransfer.currency;

import java.io.IOException;

public class CurrencyConverter {
    private double rubRate;
    private double usdRate;
    private double eurRate;

    public CurrencyConverter() throws IOException {
        defineRates();
    }

    /**
     * set all currency rates
     * @throws IOException if rates source is unavailable
     */
    private void defineRates() throws IOException {
        CurrencyJsonReader reader = new CurrencyJsonReader();
        rubRate = reader.getRubRate();
        usdRate = reader.getUsdRate();
        eurRate = reader.getEurRate();
    }

    /**
     * convert source currency amount to target currency amount
     * @param sourceCurrency source currency
     * @param sourceAmount amount of money in source currency
     * @param targetCurrency target currency
     * @return amount of money in target currency
     */
    public long convert(Currency sourceCurrency, long sourceAmount, Currency targetCurrency){
        switch (targetCurrency){
            case RUB: return getTargetAmount(sourceCurrency, sourceAmount, rubRate);
            case USD: return getTargetAmount(sourceCurrency, sourceAmount, usdRate);
            case EUR: return getTargetAmount(sourceCurrency, sourceAmount, eurRate);
            default: throw new IllegalArgumentException();
        }
    }

    /**
     * get amount of money in target currency
     * @param currency source currency
     * @param amount amount of money in source currency
     * @param targetRate target currency rate
     * @return amount of money in target currency
     */
    private long getTargetAmount(Currency currency, long amount, double targetRate){
        Double targetAmount;

        switch (currency){
            case RUB: targetAmount =  amount * rubRate / targetRate; break;
            case USD: targetAmount =  amount * usdRate / targetRate; break;
            case EUR: targetAmount =  amount * eurRate / targetRate; break;
            default: throw new IllegalArgumentException();
        }

        return targetAmount.longValue();
    }
}
