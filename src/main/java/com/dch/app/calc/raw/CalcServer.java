package com.dch.app.calc.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ִלטענטי on 17.06.2015.
 */
public class CalcServer {

    private Logger logger = LoggerFactory.getLogger(CalcServer.class);

    private final Map<Long, Long> numbersMap = new ConcurrentHashMap<>();
    private final AtomicLong lastClientId = new AtomicLong();

    private final AtomicLong opCount = new AtomicLong();
    private volatile long lastTime = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public CalcResponse doCalc(CalcRequest request) {
        if(lastTime == 0) {
            lastTime = System.currentTimeMillis();
        } else {
            long currTime = System.currentTimeMillis();
            if(currTime - lastTime >= 1000) {
                lock.lock();
                try {
                    if(currTime - lastTime >= 1000) {
                        lastTime = currTime;
                        logger.debug("count in sec: {}", opCount);
                        opCount.set(0);
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
        opCount.getAndIncrement();
        if(request.getOperation() == null) {
            return new CalcResponse("set operation type");
        }
        if(request.getClientId() == 0 && request.getOperation()!= CalcOperation.SET_VALUE) {
            return new CalcResponse("at first you must set value");
        }
        long currentClientId = request.getClientId();
        if(currentClientId == 0) {
            currentClientId = lastClientId.incrementAndGet();
        }

        long oldValue = 0;
        if(request.getOperation()!=CalcOperation.SET_VALUE) {
            oldValue = numbersMap.get(currentClientId);
        }

        long newValue = request.getOperation().calculate(oldValue, request.getNumber());

        if(request.getOperation()!=CalcOperation.GET_VALUE) {
            numbersMap.put(currentClientId, newValue);
        }
        return new CalcResponse(currentClientId, newValue);
    }
}
