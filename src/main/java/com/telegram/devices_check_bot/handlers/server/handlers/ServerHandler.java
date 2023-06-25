package com.telegram.devices_check_bot.handlers.server.handlers;

import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    @Autowired
    private PropertiesHandler propertiesHandler = new PropertiesHandler();
    @Autowired
    private EventMessageHandler eventMessageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws InterruptedException {
        System.out.println("Received message from client: " + message);
        if (message.equals("config")) {
            ctx.writeAndFlush("#config_data\n" + propertiesHandler.getAllConfigProperties());
        }else eventMessageHandler.alarmReply(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}