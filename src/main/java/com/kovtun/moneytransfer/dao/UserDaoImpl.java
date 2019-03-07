package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.dto.User;

import java.sql.*;

import static com.kovtun.moneytransfer.constant.DataBaseConstants.*;
import static com.kovtun.moneytransfer.constant.RequestConstants.*;

public class UserDaoImpl implements UserDao {
    private Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long createUser(User user) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(CREATE_USER, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, user.getFirstName());
        statement.setString(2, user.getSecondName());
        statement.setString(3, user.getPatronymicName());
        statement.setString(4, user.getPassportNum());
        statement.setDate(5, user.getBirthdate());
        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0)
            throw new SQLException("Creating user failed, no affected rows");

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                return generatedKeys.getLong(ID);
            }
            else {
                throw new SQLException("Creating user failed, no ID obtained");
            }
        }

    }

    @Override
    public boolean deleteUSer(long userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(DELETE_USER);
        statement.setLong(1, userId);
        int affectedRows = statement.executeUpdate();

        if (affectedRows == 1) {
            return true;
        }
        else return false;
    }

    @Override
    public User getUser(long userId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID);
        statement.setLong(1, userId);
        ResultSet result = statement.executeQuery();

        if (result.next()){
            return createUserFromResultSet(result);
        }
        else return null;
    }

    private User createUserFromResultSet(ResultSet result) throws SQLException {
        long id = result.getLong(ID);
        String firstName = result.getString(FIRST_NAME);
        String secondName = result.getString(SECOND_NAME);
        String patronymicName = result.getString(PATRONYMIC_NAME);
        String passportNum = result.getString(PASSPORT_NUM);
        Date birthdate = result.getDate(BIRTHDATE);

        return new User(id, firstName, secondName, patronymicName, passportNum, birthdate);
    }
}
