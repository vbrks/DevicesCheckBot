package com.telegram.devices_check_bot.handlers.server.handlers;

import com.telegram.devices_check_bot.handlers.PcIgnoreHandler;
import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import com.telegram.devices_check_bot.handlers.bot.handlers.MessageReplyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class EventMessageHandler {
    @Autowired
    private MessageReplyHandler messageReplyHandler;
    @Autowired
    private PropertiesHandler propertiesHandler;
    @Autowired
    private PcIgnoreHandler pcIgnoreHandler;

    public void alarmReply(String message) {
        String[] alarmParams = message.split("_");
        String device = alarmParams[0];
        String pcName = alarmParams[2];
        Map<String, String> users = propertiesHandler.getUserList();
        if (pcIgnoreHandler.getPcIgnoreList().containsKey(pcName)) {
            return;
        } else {
            users.forEach((username, chatId) -> sendAlarm(device, pcName, Long.parseLong(chatId)));
        }
    }

    private void sendAlarm(String device, String pcName, long chatId) {
        switch (device) {
            case "m" -> device = "мышь \uD83D\uDDB1";
            case "k" -> device = "клавиатура ⌨";
            case "h" -> device = "наушники \uD83C\uDFA7";
        }
        messageReplyHandler.sendAlarmMessage(chatId, pcName, device);
    }
}
