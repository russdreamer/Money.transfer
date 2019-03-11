package api;

import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PutMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kovtun.moneytransfer.currency.Currency;
import com.kovtun.moneytransfer.dao.DaoManager;
import com.kovtun.moneytransfer.dto.User;
import com.kovtun.moneytransfer.request.MoneyTransferRequest;
import com.kovtun.moneytransfer.response.MoneyTransferResult;
import com.kovtun.moneytransfer.response.RespStatus;
import com.kovtun.moneytransfer.response.Response;
import org.junit.BeforeClass;
import org.junit.Test;
import server.Server;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.TOP_UP;
import static com.kovtun.moneytransfer.constant.RequestConstants.TRANSFER_MONEY;
import static com.kovtun.moneytransfer.constant.RespConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static server.Server.testServer;

public class TopUpAccountTest {
    private static long accountNum = 1000_0000_0000_0000L;

    @BeforeClass
    public static void CreateUserAccounts() {
        SparkServer<Server> server = testServer;
        User user = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        DaoManager.createAccount(user, Currency.RUB);
    }

    @Test
    public void successTest() throws HttpClientException {
        String body = createBody(accountNum, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.SUCCESS, response.getStatus());
        assertEquals(MONEY_TRANSFER, response.getMessage());
        assertEquals(accountNum, response.getResult().getClientAccountNumber());
        assertFalse(response.getResult().getMoneyAmount() < 0);
        assertEquals(Currency.RUB, response.getResult().getCurrency());
    }

    @Test
    public void noTargetAccountTest() throws HttpClientException {
        String body = createBody(1111_1111_1111_1111L, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(NO_TARGET_ACCOUNTS, response.getMessage());
    }

    @Test
    public void attributeLackTest() throws HttpClientException {
        String body = createBody(accountNum, -100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(WRONG_PARAMS, response.getMessage());
    }

    private Response<MoneyTransferResult> sendServerRequest(String body) throws HttpClientException {
        PutMethod put = testServer.put(TOP_UP, body, false);
        HttpResponse httpResponse = testServer.execute(put);
        Type apiResultType = new TypeToken<Response<MoneyTransferResult>>() { }.getType();
        return new Gson().fromJson(new String(httpResponse.body()), apiResultType);
    }

    private String createBody(long accountNum, long amount, Currency currency) {
        MoneyTransferRequest request = new MoneyTransferRequest(null, accountNum, accountNum, amount, currency);
        return new Gson().toJson(request);
    }
}
