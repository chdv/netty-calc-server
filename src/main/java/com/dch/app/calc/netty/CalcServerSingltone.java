package com.dch.app.calc.netty;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public enum CalcServerSingltone {

    INSTANCE;

    private static final int CALC_THREADS_COUNT = 20;
    private static final int CALC_SLEEP = 0;
    private static final boolean CALC_ASYNC = false;

    private static ExecutorService executor = Executors.newFixedThreadPool(CALC_THREADS_COUNT);

    private final Map<Long, Long> cache = new ConcurrentHashMap<>();
    private final AtomicLong opCount = new AtomicLong();
    private volatile long lastTime = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicLong clientIdSequence = new AtomicLong();

    private final Logger logger = LoggerFactory.getLogger(CalcServerSingltone.class);

    public void calculate(ChannelHandlerContext ctx, CalcProtocol.CalcRequest request) {
        if(CALC_ASYNC) {
            executor.execute(()->calc(ctx, request));
        } else {
            calc(ctx, request);
        }
    }

    private void calc(ChannelHandlerContext ctx, CalcProtocol.CalcRequest request) {
        pause();
        ctx.writeAndFlush(Calculator.calculate(request));
        calcRequestsCount();
    }

    private void pause() {
        if(CALC_SLEEP > 0) {
            try {
                Thread.sleep(CALC_SLEEP);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void calcRequestsCount() {
        if(lastTime == 0) {
            lastTime = System.currentTimeMillis();
        } else {
            long currTime = System.currentTimeMillis();
            if(currTime - lastTime >= 1000) {
                lock.lock();
                try {
                    if(currTime - lastTime >= 1000) {
                        lastTime = currTime;
                        logger.debug("server count in sec: {}", opCount);
                        opCount.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }

        opCount.getAndIncrement();
    }

    public void putCacheValue(long id, long num) {
        cache.put(id, num);
    }

    public long getCacheValue(long id) {
        return cache.get(id);
    }

    public long getNextClientId() {
        return clientIdSequence.incrementAndGet();
    }
}
