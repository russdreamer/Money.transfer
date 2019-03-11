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
import com.kovtun.moneytransfer.response.*;
import org.junit.*;
import server.Server;

import java.lang.reflect.Type;
import java.sql.Date;

import static com.kovtun.moneytransfer.constant.RequestConstants.*;
import static com.kovtun.moneytransfer.constant.RespConstants.MONEY_TRANSFER;
import static com.kovtun.moneytransfer.constant.RespConstants.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static server.Server.testServer;

public class TransferMoneyTest {
    private User sourceUser;
    private long sourceAccountNum;
    private User targetUser;
    private long targetAccountNum;

    @Before
    public void CreateUserAccounts() {
        SparkServer<Server> server = testServer;
        sourceUser = new User("Иван", "Иванов", "Иванович", "1234567890", Date.valueOf("1990-10-20"));
        targetUser = new User("Степан", "Степанов", "Степанович", "0987654321", Date.valueOf("1970-10-20"));

        Type apiResultType = new TypeToken<Response<CreateAccountResult>>() { }.getType();
        Response<CreateAccountResult> resp = new Gson().fromJson(DaoManager.createAccount(sourceUser, Currency.RUB), apiResultType);
        sourceAccountNum = resp.getResult().getAccountNumber();
        resp = new Gson().fromJson(DaoManager.createAccount(targetUser, Currency.USD), apiResultType);
        targetAccountNum = resp.getResult().getAccountNumber();
    }

    @After
    public void CleanUserAccounts(){
        DaoManager.deleteAccount(sourceUser, sourceAccountNum);
        DaoManager.deleteAccount(targetUser, targetAccountNum);
    }

    @Test
    public void successTest() throws HttpClientException {
        DaoManager.topUpAccount(sourceAccountNum, 1000, Currency.EUR);
        String body = createBody(sourceUser, sourceAccountNum, targetAccountNum, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.SUCCESS, response.getStatus());
        assertEquals(MONEY_TRANSFER, response.getMessage());
        assertEquals(sourceAccountNum, response.getResult().getClientAccountNumber());
        assertFalse(response.getResult().getMoneyAmount() < 0);
        assertEquals(Currency.RUB, response.getResult().getCurrency());
    }

    @Test
    public void noUserAccountTest() throws HttpClientException {
        User user = new User("Антон", "Антонов", "Антонович", "344555", Date.valueOf("1990-10-20"));
        String body = createBody(user, sourceAccountNum, targetAccountNum, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(NO_USER_ACCOUNTS, response.getMessage());
    }

    @Test
    public void noTargetAccountTest() throws HttpClientException {
        String body = createBody(sourceUser, sourceAccountNum, 1111_1111_1111_1111L, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(NO_TARGET_ACCOUNTS, response.getMessage());
    }

    @Test
    public void notEnoughMoneyTest() throws HttpClientException {
        SparkServer<Server> server = testServer;
        String body = createBody(sourceUser, sourceAccountNum, targetAccountNum, 10000, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(NOT_ENOUGH_MONEY, response.getMessage());
    }

    @Test
    public void attributeLackTest() throws HttpClientException {
        User user = new User("Иван", "", "Иванович", "", Date.valueOf("1990-10-20"));
        String body = createBody(user, sourceAccountNum, targetAccountNum, 100, Currency.EUR);
        Response<MoneyTransferResult> response = sendServerRequest(body);

        assertEquals(RespStatus.ERROR, response.getStatus());
        assertEquals(WRONG_PARAMS, response.getMessage());
    }

    private Response<MoneyTransferResult> sendServerRequest(String body) throws HttpClientException {
        PutMethod put = testServer.put(TRANSFER_MONEY, body, false);
        HttpResponse httpResponse = testServer.execute(put);
        Type apiResultType = new TypeToken<Response<MoneyTransferResult>>() { }.getType();
        return new Gson().fromJson(new String(httpResponse.body()), apiResultType);
    }

    private String createBody(User user, long userAccountNum, long targetAccountNum, long amount, Currency currency) {
        MoneyTransferRequest request = new MoneyTransferRequest(user, userAccountNum, targetAccountNum, amount, currency);
        return new Gson().toJson(request);
    }
}
