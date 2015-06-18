package com.dch.app.calc.raw;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class CalcResponse {

    private long clientId;

    private long number;

    private String error;

    public CalcResponse(long clientId, long number) {
        this.clientId = clientId;
        this.number = number;
    }

    public CalcResponse(String error) {
        this.error = error;
    }

    public long getClientId() {
        return clientId;
    }

    public long getNumber() {
        return number;
    }

    public String getError() {
        return error;
    }

    @Override
    public String toString() {
        return "CalcResponse{" +
                "clientId=" + clientId +
                ", number=" + number +
                ", error='" + error + '\'' +
                '}';
    }
}
