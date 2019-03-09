package com.kovtun.moneytransfer.response;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Response {
    private RespStatus status;
    private String message;
    private Result result;

    /**
     * @param status response status
     * @param message server message
     * @param result result of transaction; <code>null</code> if status is Error
     */
    public Response(RespStatus status, String message, Result result) {
        this.status = status;
        this.message = message;
        this.result = result;
    }

    /**
     * convert class object to json
     * @return json string object with this class fields
     */
    public String toJson(){
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public RespStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Result getResult() {
        return result;
    }
}
