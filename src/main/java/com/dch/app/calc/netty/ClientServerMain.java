package com.dch.app.calc.netty;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class ClientServerMain {

    private static final int CLIENT_THREADS_COUNT = 100;

    public static void main(String[] args) throws Exception {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new CalcNettyServer().startServer();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Thread.sleep(1500);
        CalcNettyClient.runManyClients();
    }

    private static void runOneClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new CalcNettyClient().doRequests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

}
