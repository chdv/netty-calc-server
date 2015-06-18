package com.dch.app.calc.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public enum CalcServerSingltone {

    INSTANCE;

    private final Map<Long, Long> cache = new ConcurrentHashMap<>();

    private final AtomicLong opCount = new AtomicLong();
    private volatile long lastTime = 0;
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicLong clientIdSequence = new AtomicLong();

    private final Logger logger = LoggerFactory.getLogger(CalcServerSingltone.class);

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
