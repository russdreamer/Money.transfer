package server;

import com.despegar.sparkjava.test.SparkServer;
import com.kovtun.moneytransfer.AppLauncher;
import spark.Spark;
import spark.servlet.SparkApplication;

public class Server implements SparkApplication {
    public static SparkServer<Server> testServer = starAndGetServer();

    @Override
    public void init() {
        AppLauncher.main(new String[]{});
    }

    private static SparkServer<Server> starAndGetServer() {
        new Server().init();
        Spark.awaitInitialization();
        return new SparkServer<>(Server.class, com.kovtun.moneytransfer.constant.ServerConstants.DEFAULT_SERVER_PORT);
    }
}
