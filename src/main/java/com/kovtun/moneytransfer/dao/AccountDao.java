package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.dto.Account;

import java.sql.SQLException;
import java.util.Set;

public interface AccountDao {
    /**
     * create new client's account
     * @param userId account holder id
     * @return client's account id
     */
    long createAccount(long userId) throws SQLException;

    /**
     * delete a client's account
     * @param accountId client's account id
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    boolean deleteAccount(long accountId) throws SQLException;

    /**
     * get a client's account by id
     * @param accountId client's account id
     * @return client's account
     */
    Account getAccountById(long accountId) throws SQLException;

    /**
     * get all client's account
     * @param userId account holder id
     * @return set of client's accounts
     */
    Set<Account> getUserAccounts(long userId) throws SQLException;

    /**
     * change balance amount of client's account
     * @param accountId client's account id
     * @param newAmount new balance amount of money in account currency
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    boolean updateAccountBalance(long accountId, long newAmount) throws SQLException;
}
