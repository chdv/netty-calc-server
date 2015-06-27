package com.dch.app.calc.netty;

import com.dch.app.calc.netty.CalcProtocol.CalcRequest;
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
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CalcRequest request) throws Exception {
        CalcServerSingltone.INSTANCE.calculate(ctx, request);
    }

}
