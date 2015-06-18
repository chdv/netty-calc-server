package com.dch.app.calc.netty;

import com.dch.app.calc.netty.CalcProtocol.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Дмитрий on 17.06.2015.
 */
public class CalcServerHandler extends SimpleChannelInboundHandler<CalcRequest> {

    private Logger logger = LoggerFactory.getLogger(CalcServerHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CalcRequest request) throws Exception {
        CalcServerSingltone.INSTANCE.calcRequestsCount();

        CalcProtocol.CalcResponse.Builder builder = CalcProtocol.CalcResponse.newBuilder();

        if(request.getOperation() == null) {
            builder.setError("set operation type");
            ctx.write(builder.build());
            return;
        }
        if(request.getClientId() == 0 && request.getOperation()!= CalcOperation.SET_VALUE) {
            builder.setError("at first you must set value");
            ctx.write(builder.build());
            return;
        }
        long currentClientId = request.getClientId();
        if(currentClientId == 0) {
            currentClientId = CalcServerSingltone.INSTANCE.getNextClientId();
        }
        long newValue = 0;
        long oldValue = 0;
        if(request.getOperation()!=CalcOperation.SET_VALUE) {
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
            ctx.write(builder.build());
        }
        if(request.getOperation()!=CalcOperation.GET_VALUE) {
            CalcServerSingltone.INSTANCE.putCacheValue(currentClientId, newValue);
        }

        builder.setClientId(currentClientId).setNumber(newValue);

        ctx.write(builder.build());

    }

}
