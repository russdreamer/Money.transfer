package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.dto.User;

public interface UserDao {
    /**
     * create new account holder user
     * @param user account holder user
     * @return account holder id
     */
    long createUser(User user);

    /**
     * delete an account holder user by id
     * @param userId account holder id
     * @return <code>true</code> if success, <code>false</code> if failed
     */
    boolean deleteUSer(long userId);

    /**
     * get an account holder user by id
     * @param userId account holder id
     * @return account holder user
     */
    User getUser(long userId);
}
