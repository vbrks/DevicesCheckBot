package com.telegram.devices_check_bot.handlers.bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import com.telegram.devices_check_bot.handlers.PcIgnoreHandler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Slf4j
@Component
public class CallbackReplyHandler {
    private DevicesCheckBot bot;
    @Autowired
    private PcIgnoreHandler pcIgnoreHandler;
    @Autowired
    public CallbackReplyHandler(@Lazy DevicesCheckBot bot) {
        this.bot = bot;
    }

    public void replyToUser(Long chatId, String username, CallbackQuery callback) {
        String[] callbackMessage = callback.getData().split(":");
        String action = callbackMessage[0];
        String pcName = callbackMessage[1];

        if (action.equals("forget")) {
            pcIgnoreHandler.addPcToIgnoreList(pcName);
            bot.sendMessage(chatId, pcName + " добавлен в игнор на 1 час");
        }
    }
}
