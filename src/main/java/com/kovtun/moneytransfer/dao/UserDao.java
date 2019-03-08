package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.dto.User;

import java.sql.SQLException;

public interface UserDao {
    /**
     * create new account holder user
     * @param user account holder user
     * @return account holder id
     * @throws SQLException if a database access error occurs or ID key was not generated
     */
    long createUser(User user) throws SQLException;

    /**
     * delete an account holder user by id
     * @param userId account holder id
     * @return <code>true</code> if success, <code>false</code> if there is no user with this ID
     * @throws SQLException if a database access error occurs
     */
    boolean deleteUser(long userId) throws SQLException;

    /**
     * get an account holder user by id
     * @param userId account holder id
     * @return account holder user; <code>null</code> if there is no user with this ID
     * @throws SQLException if a database access error occurs
     */
    User getUser(long userId) throws SQLException;
}
