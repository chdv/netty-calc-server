package com.dch.app.calc.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class CalcClient {

    Logger logger = LoggerFactory.getLogger(CalcClient.class);

    private CalcServer server;
    private long clientId;

    private Random random = new Random();

    public CalcClient(CalcServer server) {
        this.server = server;
    }

    private CalcResponse createServerRequest(CalcRequest req) {
//        logger.debug("request:" + req.toString());
        CalcResponse response = server.doCalc(req);
//        logger.debug("response:" + response.toString());
        if(response.getError()!=null) {
            logger.error(response.getError());
        }

        return response;
    }

    private long getNextRandom() {
        return random.nextInt(100);
    }

    public void doOperations() {
        long initNumber = getNextRandom();
        CalcResponse response = createServerRequest(createInitRequest(initNumber));
        clientId = response.getClientId();
        while(true) {
            response = createServerRequest(createNextRandomRequest());
            /*try {
                Thread.sleep(1000);
            } catch (InterruptedException egnored) {

            }*/
        }
    }

    private CalcRequest createNextRandomRequest() {
        int type = random.nextInt(4);
        switch(type) {
            case 0:
                return createAddRequest(getNextRandom());
            case 1:
                return createSubstructRequest(getNextRandom());
            case 2:
                return createMultiplyRequest(getNextRandom() / 10);
            case 3:
                return createDivideRequest(getNextRandom());
        }
        throw new IllegalStateException("error");
    }

    public CalcRequest createInitRequest(long v) {
        return new CalcRequest(0, v, CalcOperation.SET_VALUE);
    }

    public CalcRequest createAddRequest(long add) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        return new CalcRequest(clientId, add, CalcOperation.ADD);
    }

    public CalcRequest createSubstructRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        return new CalcRequest(clientId, s, CalcOperation.SUBSTRUCT);
    }

    public CalcRequest createMultiplyRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        return new CalcRequest(clientId, s, CalcOperation.MULTIPLY);
    }

    public CalcRequest createDivideRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        return new CalcRequest(clientId, s, CalcOperation.MULTIPLY);
    }

    public static void main(String[] args) {
        new CalcClient(new CalcServer()).doOperations();
    }

}
