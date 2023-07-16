package com.telegram.devices_check_bot.handlers.server.handlers;

import com.telegram.devices_check_bot.handlers.PcIgnoreHandler;
import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {
    @Autowired
    private PropertiesHandler propertiesHandler = new PropertiesHandler();
    @Autowired
    private EventMessageHandler eventMessageHandler;
    @Autowired
    private PcIgnoreHandler pcIgnoreHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String message) throws InterruptedException {
        log.info("Received message from client: " + message);
        if (message.contains("config")) {
            ctx.writeAndFlush("#config_data\n" + propertiesHandler.getAllConfigProperties());
            pcIgnoreHandler.removePcFromIgnoreList(message.split("_")[1]);
        }else eventMessageHandler.alarmReply(message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
