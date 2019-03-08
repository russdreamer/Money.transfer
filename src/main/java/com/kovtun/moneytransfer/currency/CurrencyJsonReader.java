package com.kovtun.moneytransfer.currency;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.kovtun.moneytransfer.constant.JsonCurrencyConstants.*;

class CurrencyJsonReader {
    private JsonObject valutes;

    CurrencyJsonReader() throws IOException {
        valutes = defineValutes();
        if (valutes == null)
            throw new IOException();
    }

    /**
     * get valute rates json object
     * @return valute rates json object
     * @throws IOException if rates source is unavailable
     */
    private JsonObject defineValutes() throws IOException {
        URL url = new URL(CURRENCY_RATE_URL);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        JsonParser parser = new JsonParser();
        JsonElement root = parser.parse(new InputStreamReader((InputStream) request.getContent()));
        JsonObject rootObj = root.getAsJsonObject();

        return rootObj.getAsJsonObject(VALUTE);
    }

    /**
     * get USD rate value
     * @return USD rate value
     * @throws IOException if rates source is unavailable
     */
    double getUsdRate() throws IOException{
        return getRate(USD);
    }

    /**
     * get EUR rate value
     * @return EUR rate value
     * @throws IOException if rates source is unavailable
     */
    double getEurRate() throws IOException{
        return getRate(EUR);
    }

    /**
     * get RUB rate value
     * @return RUB rate value
     */
    double getRubRate(){
        return 1.0;
    }

    /**
     * get valute rate by name
     * @param valuteName valute name to get
     * @return valute rate value
     * @throws IOException if rates source is unavailable
     */
    private double getRate(String valuteName) throws IOException {
        JsonObject valuteObj = valutes.getAsJsonObject(valuteName);
        if (valuteObj != null){
            JsonElement valueObj = valuteObj.get(VALUE);
            if (valueObj != null)
                return valueObj.getAsDouble();
        }
        throw new IOException();
    }
}
