package com.telegram.devices_check_bot.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    private PropertiesHandler propertiesHandler = new PropertiesHandler();

    @Autowired
    private ClientMessageHandler clientMessageHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws InterruptedException {
        System.out.println("Received message from client: " + message);
        if (message.equals("config")) {
            ctx.writeAndFlush("#config_data\n" + propertiesHandler.getAllProperties());
        }
        clientMessageHandler.msg(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
