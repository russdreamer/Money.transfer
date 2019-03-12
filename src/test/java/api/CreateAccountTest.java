package api;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kovtun.moneytransfer.constant.ServerConstants;
import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.request.CreateAccountRequest;
import com.kovtun.moneytransfer.response.CreateAccountResult;
import com.kovtun.moneytransfer.response.RespStatus;
import com.kovtun.moneytransfer.response.Response;
import org.joda.time.DateTime;
import org.junit.Test;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.RespConstants.*;
import static org.junit.Assert.assertEquals;
import static server.Server.testServer;

public class CreateAccountTest {
    @Test
    public void successTest() throws HttpClientException {
        User user = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        String body = createBody(user, Currency.RUB);
        Response<CreateAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.SUCCESS, response.getStatus());
        assertEquals(CREATE_SUCCESS, response.getMessage());
        assertEquals(0, response.getResult().getBalance());
        assertEquals(Currency.RUB, response.getResult().getCurrency());
        assertEquals(ServerConstants.FIRST_ACCOUNT_NUMBER, response.getResult().getAccountNumber());
    }

    @Test
    public void underageTest() throws HttpClientException {
        User user = new User("Иван", "Иванов", "Иванович", "1234567890", new Date(DateTime.now().getMillis()));
        String body = createBody(user, Currency.USD);
        Response<CreateAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(UNDERAGE, response.getMessage());
    }

    @Test
    public void attributeLackTest() throws HttpClientException {
        User user = new User("Иван", "", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        String body = createBody(user, Currency.EUR);
        Response<CreateAccountResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(WRONG_PARAMS, response.getMessage());
    }

    private Response<CreateAccountResult> sendServerRequest(String body) throws HttpClientException {
        PostMethod post = testServer.post(ACCOUNT, body, false);
        HttpResponse httpResponse = testServer.execute(post);
        Type apiResultType = new TypeToken<Response<CreateAccountResult>>() { }.getType();
        return new Gson().fromJson(new String(httpResponse.body()), apiResultType);
    }

    private String createBody(User user, Currency currency) {
        CreateAccountRequest request = new CreateAccountRequest(user, currency);
        return new Gson().toJson(request);
    }
}
