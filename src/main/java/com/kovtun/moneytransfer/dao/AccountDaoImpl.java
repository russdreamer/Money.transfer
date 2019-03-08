package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dto.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static com.kovtun.moneytransfer.constant.DataBaseConstants.*;
import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.ServerConstants.FIRST_ACCOUNT_NUMBER;

public class AccountDaoImpl implements AccountDao {
    private Connection connection;
    private static AtomicLong accountNumber = new AtomicLong(FIRST_ACCOUNT_NUMBER);

    AccountDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long createAccount(long userId, Currency currency) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_ACCOUNT_QUERY)) {

            long accountNum = getNewAccountNumber();
            statement.setLong(1, accountNum);
            statement.setLong(2, 0L);
            statement.setString(3, currency.name());
            statement.setLong(4, userId);

            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0)
                throw new SQLException("Creating user failed, no affected rows");

            return accountNum;
        }
    }

    @Override
    public boolean deleteAccount(long accountId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_ACCOUNT_QUERY)){

            statement.setLong(1, accountId);
            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;
        }
    }

    @Override
    public Account getAccountById(long accountId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_ACCOUNT_BY_ID_QUERY)){

            statement.setLong(1, accountId);
            ResultSet result = statement.executeQuery();

            if (result.next()){
                return createAccountFromResultSet(result);
            }
            else return null;
        }
    }

    @Override
    public Set<Account> getUserAccounts(long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_USER_ACCOUNTS_QUERY)){

            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();

            Set<Account> accounts = new HashSet<>();
            while(result.next()){
                Account account = createAccountFromResultSet(result);
                accounts.add(account);
            }
            return accounts;
        }
    }

    @Override
    public boolean updateAccountAmount(long accountId, long newAmount) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_ACCOUNT_AMOUNT)){

            statement.setLong(1, newAmount);
            statement.setLong(2, accountId);
            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;
        }
    }

    /**
     * create Account entity from database result Set
     * @param result database result Set
     * @return Account entity
     * @throws SQLException if result Set doesn't contain necessary columns
     */
    private Account createAccountFromResultSet(ResultSet result) throws SQLException {
        long id = result.getLong(ID);
        long account = result.getLong(ACCOUNT_NUMBER);
        long amount = result.getLong(AMOUNT);
        String currency = result.getString(CURRENCY);
        long holder = result.getLong(HOLDER_ID);

        return new Account(id, account, amount, Currency.valueOf(currency), holder);
    }

    /**
     * get new account number to create
     * @return not-existed account number
     */
    private long getNewAccountNumber(){
        return accountNumber.getAndIncrement();
    }
}
