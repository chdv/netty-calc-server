package com.dch.app.calc.raw;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class CalcRequest {

    private long clientId;

    private long number;

    private CalcOperation operation;

    public CalcRequest(long clientId, long number, CalcOperation operation) {
        this.clientId = clientId;
        this.number = number;
        this.operation = operation;
    }

    public long getClientId() {
        return clientId;
    }

    public long getNumber() {
        return number;
    }

    public CalcOperation getOperation() {
        return operation;
    }

    @Override
    public String toString() {
        return "CalcRequest{" +
                "clientId=" + clientId +
                ", number=" + number +
                ", operation=" + operation +
                '}';
    }
}
