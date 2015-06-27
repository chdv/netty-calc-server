package com.dch.app.calc.netty;

/**
 * Created by ִלטענטי on 27.06.2015.
 */
public class Calculator {

    public static CalcProtocol.CalcResponse calculate(CalcProtocol.CalcRequest request) {

        CalcProtocol.CalcResponse.Builder builder = CalcProtocol.CalcResponse.newBuilder();

        if(request.getOperation() == null) {
            builder.setError("set operation type");
            return builder.build();
        }
        if(request.getClientId() == 0 && request.getOperation()!= CalcProtocol.CalcOperation.SET_VALUE) {
            builder.setError("at first you must set value");
            return builder.build();
        }
        long currentClientId = request.getClientId();
        if(currentClientId == 0) {
            currentClientId = CalcServerSingltone.INSTANCE.getNextClientId();
        }
        long newValue = 0;
        long oldValue = 0;
        if(request.getOperation()!= CalcProtocol.CalcOperation.SET_VALUE) {
            oldValue = CalcServerSingltone.INSTANCE.getCacheValue(currentClientId);
        }
        try {
            switch (request.getOperation()) {
                case MULTIPLY:
                    newValue = oldValue * request.getNumber();
                    break;
                case DIVIDE:
                    newValue = oldValue / request.getNumber();
                    break;
                case ADD:
                    newValue = oldValue + request.getNumber();
                    break;
                case SUBSTRUCT:
                    newValue = oldValue - request.getNumber();
                    break;
                case SET_VALUE:
                    newValue = request.getNumber();
                    break;
                case GET_VALUE:
                    newValue = oldValue;
                    break;
            }
        } catch(Exception e) {
            builder.setError(e.getMessage());
            return builder.build();
        }
        if(request.getOperation()!= CalcProtocol.CalcOperation.GET_VALUE) {
            CalcServerSingltone.INSTANCE.putCacheValue(currentClientId, newValue);
        }

        builder.setClientId(currentClientId).setNumber(newValue);

        return builder.build();
    }
}
