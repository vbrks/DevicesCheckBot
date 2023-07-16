package com.telegram.devices_check_bot.handlers.bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import com.telegram.devices_check_bot.handlers.PcIgnoreHandler;

import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class CallbackReplyHandler {
    private DevicesCheckBot bot;
    @Autowired
    private PropertiesHandler propertiesHandler;
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

        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");

        String alertMessageToAdmin = "Пользователь " + username + " поставил компьютер " + pcName + " в игнор!" + "\n" +
                "Время: " + LocalDateTime.now().format(format);

        String removeMessageToAdmin = "Пользователь " + username + " убрал компьютер " + pcName + " из игнора!" + "\n" +
                "Время: " + LocalDateTime.now().format(format);

        if (action.equals("forget")) {
            pcIgnoreHandler.addPcToIgnoreList(pcName);
            bot.sendMessage(chatId, "Оповещения от " + pcName + " будут игнорироваться до перезагрузки!",
                    KeyboardHandler.getCancelKeyboard(pcName));
            bot.sendMessage(propertiesHandler.getAdminChatId(), alertMessageToAdmin);

        } else if (action.equals("recall")) {
            pcIgnoreHandler.removePcFromIgnoreList(pcName);
            bot.sendMessage(propertiesHandler.getAdminChatId(), removeMessageToAdmin);
        }
    }
}
