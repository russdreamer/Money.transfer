package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.currency.CurrencyConverter;
import com.kovtun.moneytransfer.database.DBConnection;
import com.kovtun.moneytransfer.dto.Account;
import com.kovtun.moneytransfer.dto.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Set;

import static com.kovtun.moneytransfer.validator.ParamValidator.*;

public class DaoManager {
    /**
     * create new bank account. If user is new, it will also create the user
     * @param user client whose account is going to be created
     * @param accountCurrency client's account currency
     * @return result response as json
     */
    public static String createAccount(User user, String accountCurrency) {
        if (isUserFieldsNotValid(user) || isCurrencyNotValid(accountCurrency))
            return "Wrong params";

        if (isUserUnderage(user))
            return "user is underage";

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException();

            connection.setAutoCommit(false);

            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            long newAccountNum;
            AccountDao accountDao = new AccountDaoImpl(connection);

            if (existedUser == null) {
                long userId = userDao.createUser(user);
                newAccountNum = accountDao.createAccount(userId, Currency.valueOf(accountCurrency));
            }
            else newAccountNum = accountDao.createAccount(existedUser.getId(), Currency.valueOf(accountCurrency));

            connection.commit();
            return "Success" + newAccountNum;

        } catch (SQLException e) {
            return "database access error occurs";
        }
    }

    /**
     * delete bank account. If it's the last user's account, it will also delete the user
     * @param user client whose account is going to be deleted
     * @return result response as json
     */
    public static String deleteAccount(User user, String userAccount) {
        if (isUserFieldsNotValid(user) || isNotValidLong(userAccount))
            return "Wrong params";

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException();

            connection.setAutoCommit(false);
            /* check if user and account are related */
            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            if (existedUser == null)
                return "There is no user found with these parameters";

            AccountDao accountDao = new AccountDaoImpl(connection);
            Set<Account> accountSet = accountDao.getUserAccounts(existedUser.getId());
            if (accountSet.isEmpty())
                return "No user's accounts are found";

            long accountNum = Long.valueOf(userAccount);
            Optional<Account> account = accountSet.parallelStream()
                    .filter(it ->
                            it.getAccount() == accountNum)
                    .findFirst();

            if ( !account.isPresent() )
                return "User account with this number is not found";

            if (accountSet.size() == 1)
                userDao.deleteUser(existedUser.getId());

            accountDao.deleteAccount(accountNum);

            connection.commit();
            return "Success";

        } catch (SQLException e) {
            return "database access error occurs";
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
            return "Wrong params";

        long userAccountNum = Long.valueOf(userAccountStr);
        long targetAccountNum = Long.valueOf(targetAccountNumStr);
        long amount = Long.valueOf(amountStr);
        Currency currency = Currency.valueOf(currencyStr);

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException();

            connection.setAutoCommit(false);
            /* check if user and account are related */
            UserDao userDao = new UserDaoImpl(connection);
            User existedUser = userDao.getUser(user.getId());
            if (existedUser == null)
                return "There is no user found with these parameters";

            AccountDao accountDao = new AccountDaoImpl(connection);
            Account sourceAccount = accountDao.getAccountById(userAccountNum);

            if (sourceAccount == null || existedUser.getId() == sourceAccount.getHolderId())
                return "User account with this number is not found";

            Account targetAccount = accountDao.getAccountById(targetAccountNum);
            if (targetAccount == null)
                return "No target account is found";

            /* money transfer */
            CurrencyConverter currentMoneyRate = new CurrencyConverter();

            if ( isEnoughMoney(sourceAccount, currency, amount, currentMoneyRate) ){
                updateUserBalance(accountDao, sourceAccount, 0 - amount, currency, currentMoneyRate);
                updateUserBalance(accountDao, targetAccount, amount, currency, currentMoneyRate);
            }
            else {
                return "Not enough money for the operation";
            }

            connection.commit();
            return "Success";

        } catch (SQLException | IOException e) {
            return "database access error occurs";
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
            return "Wrong params";

        long accountNum = Long.valueOf(accountNumStr);
        long amount = Long.valueOf(amountStr);
        Currency currency = Currency.valueOf(currencyStr);

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null)
                throw new SQLException();

            connection.setAutoCommit(false);
            AccountDao accountDao = new AccountDaoImpl(connection);
            Account targetAccount = accountDao.getAccountById(accountNum);

            if (targetAccount == null)
                return "No target account is found";

            CurrencyConverter currentMoneyRate = new CurrencyConverter();
            updateUserBalance(accountDao, targetAccount, amount, currency, currentMoneyRate);

            connection.commit();
            return "Success";

        } catch (SQLException | IOException e) {
            return "database access error occurs";
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
    private static void updateUserBalance(AccountDao accountDao, Account account, long amount, Currency currency, CurrencyConverter moneyRate) throws SQLException {
        long amountInUserCur = moneyRate.convert(currency, amount, account.getCurrency());
        long newSourceAmount = account.getAmount() + amountInUserCur;
        accountDao.updateAccountAmount(account.getId(), newSourceAmount);
    }
}
