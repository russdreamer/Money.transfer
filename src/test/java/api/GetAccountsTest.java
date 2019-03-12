package api;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kovtun.moneytransfer.constant.RespConstants;
import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dao.DaoManager;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.response.GetAccountsResult;
import com.kovtun.moneytransfer.response.RespStatus;
import com.kovtun.moneytransfer.response.Response;
import org.junit.Before;
import org.junit.Test;
import server.Server;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static org.junit.Assert.assertEquals;
import static server.Server.testServer;

public class GetAccountsTest {
    @Before
    public void CreateUserAccount() {
        SparkServer<Server> server = testServer;
        User user = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        DaoManager.createAccount(user, Currency.RUB);
    }

    @Test
    public void successTest() throws HttpClientException {
        User user = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        String body = createBody(user);
        Response<GetAccountsResult> response = sendServerRequest(body);

        assertEquals(RespStatus.SUCCESS, response.getStatus());
        assertEquals(RespConstants.GET_ACCOUNTS, response.getMessage());
        assertEquals(1, response.getResult().getClientAccounts().size());
    }

    @Test
    public void attributeLackTest() throws HttpClientException{
        User user = new User("Иван", "", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        String body = createBody(user);
        Response<GetAccountsResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(RespConstants.WRONG_PARAMS, response.getMessage());
    }

    @Test
    public void noUserAccountsTest() throws HttpClientException{
        User user = new User("Степан", "Степанов", "Степанович", "0987654321", Date.valueOf("1970-10-20"));
        String body = createBody(user);
        Response<GetAccountsResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(RespConstants.NO_USER_ACCOUNTS, response.getMessage());
    }

    private Response<GetAccountsResult> sendServerRequest(String body) throws HttpClientException {
        GetMethod get = testServer.get(ACCOUNTS + body, false);
        HttpResponse httpResponse = testServer.execute(get);
        Type apiResultType = new TypeToken<Response<GetAccountsResult>>() { }.getType();
        return new Gson().fromJson(new String(httpResponse.body()), apiResultType);
    }

    private String createBody(User user) {
        return "?" +
                FIRST_NAME + "=" + user.getFirstName() + "&" +
                SECOND_NAME + "=" + user.getSecondName() + "&" +
                PATRONYMIC_NAME + "=" + user.getPatronymicName() + "&" +
                PASSPORT_NUM + "=" + user.getPassportNum() + "&" +
                BIRTHDATE + "=" + user.getBirthdate();
    }
}
