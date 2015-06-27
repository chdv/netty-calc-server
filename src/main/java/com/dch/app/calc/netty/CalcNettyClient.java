package com.dch.app.calc.netty;

import com.dch.app.calc.netty.CalcProtocol.CalcOperation;
import com.dch.app.calc.netty.CalcProtocol.CalcRequest;
import com.dch.app.calc.netty.CalcProtocol.CalcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.util.Random;

/**
 * Created by Дмитрий on 17.06.2015.
 */
public class CalcNettyClient {

    private static final int REQUESTS_COUNT = 100_000;

    private Logger logger = LoggerFactory.getLogger(CalcNettyClient.class);

    static final boolean SSL = true;//System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8463"));
    private SslContext sslCtx;


    private Random random = new Random();
    private long clientId;

    public CalcNettyClient() throws SSLException, InterruptedException {
        if (SSL) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
    }

    private long getNextRandom() {
        return random.nextInt(100) + 1;
    }

    public void doRequests() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new CalcClientInitializer(sslCtx));

            Channel ch = b.connect(HOST, PORT).sync().channel();

            CalcResponse response = sendRequest(ch, createInitRequest(getNextRandom()));
            clientId = response.getClientId();

            for(int i = 0; i<REQUESTS_COUNT; i++) {
                response = sendRequest(ch, createNextRandomRequest());
//                Thread.sleep(1000);
            }

            ch.close();

        } finally {
            group.shutdownGracefully();
        }
    }

    private CalcResponse sendRequest(Channel ch, CalcRequest request) {
//        logger.debug("request: {}", request.toString());
        CalcClientHandler handler = ch.pipeline().get(CalcClientHandler.class);
        CalcResponse response = handler.sendRequest(request);
//        calcRequestsCount();
//        logger.debug("response: {}", response.toString());
        return response;
    }

    private CalcRequest createNextRandomRequest() {
        int type = random.nextInt(4);
        switch(type) {
            case 0:
                return createAddRequest(getNextRandom());
            case 1:
                return createSubstructRequest(getNextRandom());
            case 2:
                return createMultiplyRequest(getNextRandom());
            case 3:
                return createDivideRequest(getNextRandom());
        }
        throw new IllegalStateException("error");
    }

    public CalcRequest createInitRequest(long v) {
        CalcRequest.Builder builder = CalcRequest.newBuilder();
        return builder.
                setClientId(0).
                setNumber(v).
                setOperation(CalcOperation.SET_VALUE).build();
    }

    public CalcRequest createAddRequest(long add) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        CalcRequest.Builder builder = CalcRequest.newBuilder();
        return builder.
                setClientId(clientId).
                setNumber(add).
                setOperation(CalcOperation.ADD).build();
    }

    public CalcRequest createSubstructRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        CalcRequest.Builder builder = CalcRequest.newBuilder();
        return builder.
                setClientId(clientId).
                setNumber(s).
                setOperation(CalcOperation.SUBSTRUCT).build();
    }

    public CalcRequest createMultiplyRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        CalcRequest.Builder builder = CalcRequest.newBuilder();
        return builder.
                setClientId(clientId).
                setNumber(s).
                setOperation(CalcOperation.MULTIPLY).build();
    }

    public CalcRequest createDivideRequest(long s) {
        if(clientId == 0)
            throw new IllegalStateException("clientId must not be null");
        CalcRequest.Builder builder = CalcRequest.newBuilder();
        return builder.
                setClientId(clientId).
                setNumber(s).
                setOperation(CalcOperation.DIVIDE).build();
    }

    private static final int CLIENT_THREADS_COUNT = 100;

    public static void runManyClients() {
        for(int i = 0; i<CLIENT_THREADS_COUNT; i++) {
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


    public static void runOneClient() {
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

    public static void main(String[] args) throws Exception {
        runOneClient();
    }

}
