package com.dch.app.calc.raw;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class ClientsManager {

    private static final int THREADS_COUNT = Runtime.getRuntime().availableProcessors() * 4;

    private CalcClient[] clients = new CalcClient[THREADS_COUNT];
    private Thread[] threads = new Thread[THREADS_COUNT];
    private CalcServer server = new CalcServer();

    public ClientsManager() {

    }

    public void runRequests() {
        for(int i = 0; i<THREADS_COUNT; i++) {
            clients[i] = new CalcClient(server);
            threads[i] = new Thread(new ClientRunnable(clients[i]));
            threads[i].start();
        }
    }

    private class ClientRunnable implements Runnable {

        private CalcClient client;

        ClientRunnable(CalcClient client) {
            this.client = client;
        }

        @Override
        public void run() {
            client.doOperations();
        }
    }

    public static void main(String[] args) {
        new ClientsManager().runRequests();
    }
}
