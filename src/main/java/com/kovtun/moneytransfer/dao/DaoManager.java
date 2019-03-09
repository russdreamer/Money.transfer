package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.currency.CurrencyConverter;
import com.kovtun.moneytransfer.database.DBConnection;
import com.kovtun.moneytransfer.dto.Account;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.response.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import static com.kovtun.moneytransfer.constant.RespConstants.*;
import static com.kovtun.moneytransfer.validator.ParamValidator.*;

public class DaoManager {
    /**
     * create new bank account. If user is new, it will also create the user
     * @param user client whose account is going to be created
     * @param accountCurrencyStr client's account currency
     * @return result response as json
     */
    public static String createAccount(User user, String accountCurrencyStr) {
        if (isUserFieldsNotValid(user) || isCurrencyNotValid(accountCurrencyStr))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        if (isUserUnderage(user))
            return new Response(RespStatus.ERROR, UNDERAGE, null).toJson();

        Currency accountCurrency = Currency.valueOf(accountCurrencyStr);

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);

            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            long newAccountNum;
            AccountDao accountDao = new AccountDaoImpl(connection);

            if (existedUser == null) {
                long userId = userDao.createUser(user);
                newAccountNum = accountDao.createAccount(userId, accountCurrency);
            }
            else newAccountNum = accountDao.createAccount(existedUser.getId(), accountCurrency);

            connection.commit();
            return new Response(RespStatus.SUCCESS, CREATE_SUCCESS,
                    new CreateAccountResult(newAccountNum, 0, accountCurrency))
                    .toJson();

        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(RespStatus.ERROR, DB_ACCESS_ERROR, null).toJson();
        }
    }

    /**
     * delete bank account. If it's the last user's account, it will also delete the user
     * @param user client whose account is going to be deleted
     * @return result response as json
     */
    public static String deleteAccount(User user, String userAccount) {
        if (isUserFieldsNotValid(user) || isNotValidLong(userAccount))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);
            /* check if user and account are related */
            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            if (existedUser == null)
                return new Response(RespStatus.ERROR, NO_USER, null).toJson();

            AccountDao accountDao = new AccountDaoImpl(connection);
            Set<Account> accountSet = accountDao.getUserAccounts(existedUser.getId());
            if (accountSet.isEmpty())
                return new Response(RespStatus.ERROR, NO_USER_ACCOUNTS, null).toJson();

            long accountNum = Long.valueOf(userAccount);
            Optional<Account> account = accountSet.parallelStream()
                    .filter(it ->
                            it.getAccount() == accountNum)
                    .findFirst();

            if ( !account.isPresent() )
                return new Response(RespStatus.ERROR, NO_USER, null).toJson();

            if (accountSet.size() == 1)
                userDao.deleteUser(existedUser.getId());

            accountDao.deleteAccount(accountNum);

            connection.commit();

            Result result = new DeleteAccountResult(accountNum, account.get().getAmount(),
                    account.get().getCurrency(), accountSet.size() > 1);
            return new Response(RespStatus.SUCCESS, DELETE_SUCCESS, result).toJson();

        } catch (SQLException e) {
            e.printStackTrace();
            return new Response(RespStatus.ERROR, DB_ACCESS_ERROR, null).toJson();
        }
    }

    /**
     * transfer money between accounts of the only user
     * @param user client whose money are being moved between his accounts
     * @param userAccountStr client account to be charged
     * @param targetAccountNumStr account to be refilled
     * @param amountStr amount of money
     * @param currencyStr currency in which the transaction is performed
     * @return result response as json
     */
    public static String transferMoney(User user, String userAccountStr, String targetAccountNumStr, String amountStr, String currencyStr) {
        if (isUserFieldsNotValid(user) || isNotValidLong(userAccountStr, targetAccountNumStr, amountStr) || isCurrencyNotValid(currencyStr))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        long userAccountNum = Long.valueOf(userAccountStr);
        long targetAccountNum = Long.valueOf(targetAccountNumStr);
        long amount = Long.valueOf(amountStr);
        Currency currency = Currency.valueOf(currencyStr);

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);
            /* check if user and account are related */
            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            if (existedUser == null)
                return new Response(RespStatus.ERROR, NO_USER, null).toJson();

            AccountDao accountDao = new AccountDaoImpl(connection);
            Account sourceAccount = accountDao.getAccountById(userAccountNum);

            if (sourceAccount == null || existedUser.getId() == sourceAccount.getHolderId())
                return new Response(RespStatus.ERROR, NO_USER_ACCOUNTS, null).toJson();

            Account targetAccount = accountDao.getAccountById(targetAccountNum);
            if (targetAccount == null)
                return new Response(RespStatus.ERROR, NO_TARGET_ACCOUNTS, null).toJson();

            /* money transfer */
            CurrencyConverter currentMoneyRate = new CurrencyConverter();

            long newUserAmount;
            if ( isEnoughMoney(sourceAccount, currency, amount, currentMoneyRate) ){
                newUserAmount = updateUserBalance(accountDao, sourceAccount, 0 - amount, currency, currentMoneyRate);
                updateUserBalance(accountDao, targetAccount, amount, currency, currentMoneyRate);
            }
            else {
                return new Response(RespStatus.ERROR, NOT_ENOUGH_MONEY, null).toJson();
            }

            connection.commit();
            return new Response(RespStatus.SUCCESS, MONEY_TRANSFER,
                    new MoneyTransferResult(userAccountNum, newUserAmount, sourceAccount.getCurrency()))
                    .toJson();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return new Response(RespStatus.ERROR, DB_ACCESS_ERROR, null).toJson();
        }
    }

    /**
     * refilling account balance
     * @param accountNumStr client account to be refilled
     * @param amountStr amount of money
     * @param currencyStr currency in which the transaction is performed
     * @return result response as json
     */
    public static String topUpAccount(String accountNumStr, String amountStr, String currencyStr) {
        if ( isNotValidLong(accountNumStr, amountStr) || isCurrencyNotValid(currencyStr))
            return new Response(RespStatus.ERROR, WRONG_PARAMS, null).toJson();

        long accountNum = Long.valueOf(accountNumStr);
        long amount = Long.valueOf(amountStr);
        Currency currency = Currency.valueOf(currencyStr);

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);
            AccountDao accountDao = new AccountDaoImpl(connection);
            Account targetAccount = accountDao.getAccountById(accountNum);

            if (targetAccount == null)
                return new Response(RespStatus.ERROR, NO_TARGET_ACCOUNTS, null).toJson();

            CurrencyConverter currentMoneyRate = new CurrencyConverter();
            long newAmount = updateUserBalance(accountDao, targetAccount, amount, currency, currentMoneyRate);

            connection.commit();
            return new Response(RespStatus.SUCCESS, MONEY_TRANSFER,
                    new MoneyTransferResult(accountNum, newAmount, targetAccount.getCurrency()))
                    .toJson();

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return new Response(RespStatus.ERROR, DB_ACCESS_ERROR, null).toJson();
        }
    }

    /**
     * update user's account money amount
     * @param accountDao dao with connection
     * @param account user account entity
     * @param amount amount to increase user's balance
     * @param currency amount currency to increase
     * @param moneyRate current money rates
     * @throws SQLException if a database access error occurs
     */
    private static long updateUserBalance(AccountDao accountDao, Account account, long amount, Currency currency, CurrencyConverter moneyRate) throws SQLException {
        long amountInUserCur = moneyRate.convert(currency, amount, account.getCurrency());
        long newSourceAmount = account.getAmount() + amountInUserCur;
        accountDao.updateAccountAmount(account.getId(), newSourceAmount);

        return newSourceAmount;
    }
}
