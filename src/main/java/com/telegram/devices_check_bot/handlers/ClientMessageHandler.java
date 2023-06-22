package com.telegram.devices_check_bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientMessageHandler {

    @Autowired
    private DevicesCheckBot devicesCheckBot;
    public void msg(String message) {


        long chatId = 452197607L;

        if (message.contains("disabled")) {
            String[] alarmParams = message.split("_");
            String device = alarmParams[0];
            switch (device){
                case "m" -> device = "мышь \uD83D\uDDB1";
                case "k" -> device = "клавиатура ⌨";
                case "h" -> device = "наушники \uD83C\uDFA7";
            }

            String pcName = alarmParams[2];
            devicesCheckBot.sendAlarmMessage(chatId, pcName, device);
        }
    }
}
