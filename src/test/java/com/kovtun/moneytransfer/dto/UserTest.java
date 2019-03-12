package com.kovtun.moneytransfer.dto;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;

public class UserTest {
    @Test
    public void dtoGettersTest(){
        Long id = 1L;
        String firstName = "Ivan";
        String secondName = "Ivanov";
        String patronymicName = "Ivanovich";
        String passportNum = "12345";
        Date birthdate = new Date(DateTime.now().getMillis());
        User user = new User(id,firstName, secondName, patronymicName, passportNum, birthdate);

        Assert.assertEquals(user.getId(), id);
        Assert.assertEquals(user.getFirstName(), firstName);
        Assert.assertEquals(user.getSecondName(), secondName);
        Assert.assertEquals(user.getPatronymicName(), patronymicName);
        Assert.assertEquals(user.getPassportNum(), passportNum);
        Assert.assertSame(user.getBirthdate(), birthdate);
    }
}
