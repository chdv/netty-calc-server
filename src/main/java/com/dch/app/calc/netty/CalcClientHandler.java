package com.dch.app.calc.netty;

import com.dch.app.calc.netty.CalcProtocol.CalcRequest;
import com.dch.app.calc.netty.CalcProtocol.CalcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Дмитрий on 17.06.2015.
 */
public class CalcClientHandler extends SimpleChannelInboundHandler<CalcResponse> {

    // Stateful properties
    private Channel channel;
    private ReentrantLock lock = new ReentrantLock();
    private Condition serverResponse = lock.newCondition();
    private CalcResponse response = null;

    public CalcClientHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CalcResponse response) throws Exception {
        this.response = response;
        lock.lock();
        try {
            serverResponse.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        channel = ctx.channel();
    }

    public CalcResponse sendRequest(CalcRequest request) {
        channel.writeAndFlush(request);

        lock.lock();
        try {
            serverResponse.await();
        } catch (InterruptedException ignored) {
            /*NOP*/
        } finally {
            lock.unlock();
        }
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
