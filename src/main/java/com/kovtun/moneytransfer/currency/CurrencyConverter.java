package com.kovtun.moneytransfer.currency;

public class CurrencyConverter {
    private long rubRate;
    private long usdRate;
    private long eurRate;

    {
        defineRates();
    }

    private void defineRates() {
        rubRate = 1;
        usdRate = 65;
        eurRate = 70;
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
     * @param targetCurrency target currency
     * @return amount of money in target currency
     */
    private long getTargetAmount(Currency currency, long amount, long targetCurrency){
        switch (currency){
            case RUB: return amount * rubRate / targetCurrency;
            case USD: return amount * usdRate / targetCurrency;
            case EUR: return amount * eurRate / targetCurrency;
            default: throw new IllegalArgumentException();
        }
    }
}
