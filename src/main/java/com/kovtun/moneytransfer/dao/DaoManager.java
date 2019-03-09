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
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kovtun.moneytransfer.constant.RespConstants.*;
import static com.kovtun.moneytransfer.validator.ParamValidator.*;

public class DaoManager {
    /**
     * create new bank account. If user is new, it will also create the user
     * @param user client whose account is going to be created
     * @param accountCurrency client's account currency
     * @return result response as json
     */
    public static String createAccount(User user, Currency accountCurrency) {
        if (isUserUnderage(user))
            return new Response(RespStatus.ERROR, UNDERAGE, null).toJson();

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);

            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user);
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
    public static String deleteAccount(User user, long accountNum) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);

            Set<Account> accountSet = getUserAccounts(connection, user);
            if (accountSet == null || accountSet.isEmpty())
                return new Response(RespStatus.ERROR, NO_USER_ACCOUNTS, null).toJson();

            Optional<Account> account = accountSet.parallelStream()
                    .filter(it -> it.getAccount() == accountNum)
                    .findFirst();

            if ( !account.isPresent() )
                return new Response(RespStatus.ERROR, NO_USER, null).toJson();

            /* if it is the only user's account - delete user as well */
            if (accountSet.size() == 1)
                new UserDaoImpl(connection).deleteUser(account.get().getHolderId());

            new AccountDaoImpl(connection).deleteAccount(accountNum);
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
     * @param user client whose money are being charged
     * @param userAccountNum client account to be charged
     * @param targetAccountNum account to be refilled
     * @param amount amount of money
     * @param currency currency in which the transaction is performed
     * @return result response as json
     */
    public static String transferMoney(User user, long userAccountNum, long targetAccountNum, long amount, Currency currency) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);

            Account sourceAccount = getUserAccount(connection, user, userAccountNum);
            if (sourceAccount == null)
                return new Response(RespStatus.ERROR, NO_USER_ACCOUNTS, null).toJson();

            AccountDao accountDao = new AccountDaoImpl(connection);
            Account targetAccount = accountDao.getAccountById(targetAccountNum);
            if (targetAccount == null)
                return new Response(RespStatus.ERROR, NO_TARGET_ACCOUNTS, null).toJson();

            CurrencyConverter currentMoneyRate = new CurrencyConverter();
            if ( isEnoughMoney(sourceAccount, currency, amount, currentMoneyRate) )
                return new Response(RespStatus.ERROR, NOT_ENOUGH_MONEY, null).toJson();

            long newUserAmount = updateUserBalance(accountDao, sourceAccount, 0 - amount, currency, currentMoneyRate);
            updateUserBalance(accountDao, targetAccount, amount, currency, currentMoneyRate);

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
     * @param accountNum client account to be refilled
     * @param amount amount of money
     * @param currency currency in which the transaction is performed
     * @return result response as json
     */
    public static String topUpAccount(long accountNum, long amount, Currency currency) {
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
     * get all client's accounts with detailed account info
     * @param user accounts holder
     * @return result response as json
     */
    public static String getAccounts(User user) {
        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException(CONNECTION_NULL);

            connection.setAutoCommit(false);

            Set<Account> accountSet = getUserAccounts(connection, user);
            if (accountSet == null || accountSet.isEmpty())
                return new Response(RespStatus.ERROR, NO_USER_ACCOUNTS, null).toJson();

            List<Account> censuredAccounts = accountSet.parallelStream()
                    .map(account ->
                    new Account(null, account.getAccount(), account.getAmount(), account.getCurrency(), null))
                    .collect(Collectors.toList());

            connection.commit();
            return new Response(RespStatus.SUCCESS, GET_ACCOUNTS,
                    new GetAccountsResult(censuredAccounts))
                    .toJson();

        } catch (SQLException e) {
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

    /**
     * get all client's accounts from database
     * @param connection database connection
     * @param user user entity without id
     * @return Set of client's accounts
     * @throws SQLException if a database access error occurs
     */
    private static Set<Account> getUserAccounts(Connection connection, User user) throws SQLException {
        UserDao userDao = new UserDaoImpl(connection);
        User existedClient = userDao.getUser(user);
        if (existedClient == null)
            return null;

        AccountDao accountDao = new AccountDaoImpl(connection);
        return accountDao.getUserAccounts(existedClient.getId());
    }

    /**
     * get client's account by account number from database
     * @param connection database connection
     * @param user user entity without id
     * @param userAccountNum user's account number
     * @return client's account
     * @throws SQLException if a database access error occurs
     */
    private static Account getUserAccount(Connection connection, User user, long userAccountNum) throws SQLException {
        UserDao userDao = new UserDaoImpl(connection);
        User existedUser = userDao.getUser(user);
        if (existedUser == null)
            return null;

        AccountDao accountDao = new AccountDaoImpl(connection);
        return accountDao.getAccountById(userAccountNum);
    }
}
