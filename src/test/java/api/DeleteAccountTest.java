package api;

import com.despegar.http.client.DeleteMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kovtun.moneytransfer.constant.RespConstants;
import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dao.DaoManager;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.response.CreateAccountResult;
import com.kovtun.moneytransfer.response.DeleteAccountResult;
import com.kovtun.moneytransfer.response.RespStatus;
import com.kovtun.moneytransfer.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import server.Server;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.RespConstants.DELETE_SUCCESS;
import static com.kovtun.moneytransfer.constant.RespConstants.WRONG_PARAMS;
import static org.junit.Assert.assertEquals;
import static server.Server.testServer;

public class DeleteAccountTest {
    private User user;
    private long accountNum;

    @Before
    public void CreateUserAccount() {
        SparkServer<Server> server = testServer;
        user = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));

        Type apiResultType = new TypeToken<Response<CreateAccountResult>>() { }.getType();
        Response<CreateAccountResult> resp = new Gson().fromJson(DaoManager.createAccount(user, Currency.RUB), apiResultType);
        accountNum = resp.getResult().getAccountNumber();
    }

    @After
    public void CleanUserAccounts(){
        DaoManager.deleteAccount(user, accountNum);
    }


    @Test
    public void successTest() throws HttpClientException {
        String body = createBody(user, accountNum);
        Response<DeleteAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.SUCCESS, response.getStatus());
        assertEquals(DELETE_SUCCESS, response.getMessage());
        assertEquals(0, response.getResult().getAvailableFunds());
        assertEquals(Currency.RUB, response.getResult().getCurrency());
        assertEquals(accountNum, response.getResult().getRemovedAccount());
    }

    @Test
    public void noSuchUserTest() throws HttpClientException {
        User user = new User("Степан", "Степанов", "Степанович", "67890", Date.valueOf("1990-10-20"));
        String body = createBody(user, accountNum);
        Response<DeleteAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(RespConstants.NO_USER_ACCOUNTS, response.getMessage());
    }

    @Test
    public void attributeLackTest() throws HttpClientException {
        User user = new User("Иван", "", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        String body = createBody(user, accountNum);
        Response<DeleteAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(WRONG_PARAMS, response.getMessage());
    }

    private Response<DeleteAccountResult> sendServerRequest(String body) throws HttpClientException {
        DeleteMethod delete = testServer.delete(ACCOUNT + body, false);

        HttpResponse httpResponse = testServer.execute(delete);
        Type apiResultType = new TypeToken<Response<DeleteAccountResult>>() { }.getType();
        return new Gson().fromJson(new String(httpResponse.body()), apiResultType);
    }

    private String createBody(User user, long accountNum) {
        return "?" +
                FIRST_NAME + "=" + user.getFirstName() + "&" +
                SECOND_NAME + "=" + user.getSecondName() + "&" +
                PATRONYMIC_NAME + "=" + user.getPatronymicName() + "&" +
                PASSPORT_NUM + "=" + user.getPassportNum() + "&" +
                BIRTHDATE + "=" + user.getBirthdate() + "&" +
                ACCOUNT_NUMBER + "=" + accountNum;
    }
}
