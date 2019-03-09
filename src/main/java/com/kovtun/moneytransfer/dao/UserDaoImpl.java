package com.kovtun.moneytransfer.dao;

import com.kovtun.moneytransfer.dto.User;

import java.sql.*;

import static com.kovtun.moneytransfer.constant.DataBaseConstants.*;
import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.RespConstants.NO_AFFECTED_ROWS;
import static com.kovtun.moneytransfer.constant.RespConstants.NO_ID_OBTAINED;

public class UserDaoImpl implements UserDao {
    private Connection connection;

    UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public long createUser(User user) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(CREATE_USER_QUERY, Statement.RETURN_GENERATED_KEYS)){

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getSecondName());
            statement.setString(3, user.getPatronymicName());
            statement.setString(4, user.getPassportNum());
            statement.setDate(5, user.getBirthdate());
            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0)
                throw new SQLException(NO_AFFECTED_ROWS);

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(ID);
                }
                else {
                    throw new SQLException(NO_ID_OBTAINED);
                }
            }
        }
    }

    @Override
    public boolean deleteUser(long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USER_QUERY)){

            statement.setLong(1, userId);
            int affectedRows = statement.executeUpdate();

            return affectedRows == 1;
        }
    }

    @Override
    public User getUser(long userId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(GET_USER_BY_ID_QUERY)){

            statement.setLong(1, userId);
            ResultSet result = statement.executeQuery();

            if (result.next()){
                return createUserFromResultSet(result);
            }
            else return null;
        }
    }

    /**
     * create User entity from database result Set
     * @param result database result Set
     * @return User entity
     * @throws SQLException if result Set doesn't contain necessary columns
     */
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
