package com.kovtun.moneytransfer.validator;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.currency.CurrencyConverter;
import com.kovtun.moneytransfer.dto.Account;
import com.kovtun.moneytransfer.dto.User;
import org.joda.time.DateTime;
import java.sql.Date;
import java.time.LocalDate;

public class ParamValidator {
    /**
     * check if any of user's field is valid
     * @param user client to create
     * @return <code>true</code>  if any field is not valid; <code>false</code> if all the fields are valid
     */
    public static boolean isUserFieldsNotValid(User user) {
        return user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
                user.getSecondName() == null || user.getSecondName().trim().isEmpty() ||
                user.getPatronymicName() == null || user.getPatronymicName().trim().isEmpty() ||
                user.getPassportNum() == null || user.getPassportNum().trim().isEmpty() ||
                user.getBirthdate() == null;
    }

    /**
     * check if any of the objects is null
     * @param objects objects to check
     * @return <code>true</code>  if any object is null; <code>false</code> if all the fields aren't null
     */
    public static boolean isNull(Object... objects) {
        for (Object object: objects) {
            if (object == null)
                return true;
        }
        return false;
    }

    /**
     * check if user is underAge
     * @param user client to create
     * @return <code>true</code> if client is underAge; <code>false</code> if client is adult
     */
    public static boolean isUserUnderage(User user) {
        return getCurrentAge(user.getBirthdate()) < 18;
    }

    /**
     * get current client age related on his birth date
     * @param userBirthdate client's date of the birth
     * @return current client age
     */
    private static int getCurrentAge(Date userBirthdate) {
        LocalDate userDate = userBirthdate.toLocalDate();
        DateTime now =  DateTime.now();
        int diff = now.year().get() - userDate.getYear();

        if (now.dayOfYear().get() <= userDate.getDayOfYear())
            diff--;

        return diff;
    }

    /**
     * check if any of fields is not valid long values
     * @param longValue values to check
     * @return <code>true</code> if any field is not valid; <code>false</code> if all the fields are valid
     */
    public static boolean isNotValidLong(String... longValue){
        for (String item: longValue) {
            try {
                Long.valueOf(item);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if user has enough money to transfer
     * @param userAccount client account entity
     * @param targetCur money currency to transfer
     * @param targetAmount amount of money in target currency to transfer
     * @param moneyRate current money rates
     * @return <code>true</code> if client has enough money for transaction;
     * <code>false</code> if client doesn't have enough money
     */
    public static boolean isNotEnoughMoney(Account userAccount, Currency targetCur, long targetAmount, CurrencyConverter moneyRate){
        long amountInUserCur = moneyRate.convert(targetCur, targetAmount, userAccount.getCurrency());
        long nextAccountBalance = userAccount.getAmount() - amountInUserCur;
        return nextAccountBalance < 0;
    }
}
