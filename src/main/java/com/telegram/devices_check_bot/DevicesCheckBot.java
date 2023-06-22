package com.telegram.devices_check_bot;

import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class DevicesCheckBot extends TelegramLongPollingBot {
    @Autowired
    PropertiesHandler propertiesHandler;
    @Value("${admin}")
    String adminUsername;

    public DevicesCheckBot(@Value("${bot.token}") String botToken) {super(botToken); }

    @Override
    public String getBotUsername() {
        return "DevicesCheckBot";
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        System.out.println(update);
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String username = update.getMessage().getChat().getUserName();
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        switch (message) {
            case "/start" -> startCommand(chatId, username);
            case "/help" -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
        if(username.equals(adminUsername)) {
            if (message.contains("/add_mouses")) {
                addMouses(chatId, message);
            } else if (message.contains("/add_keyboards")) {
                addKeyboards(chatId, message);
            } else if (message.contains("/add_headphones")) {
                addHeadphones(chatId, message);
            } else if (message.contains("/set_listen_timeout")) {
                setListenDelay(chatId, message.replace("/set_listen_timeout", "").replace(" ", ""));
            } else if (message.contains("/set_alarm_timeout")) {
                setAlarmDelay(chatId, message);
            }
        }
    }
    private void addMouses(Long chatId, String msg){
        msg = msg.replace("/add_mouses", "").replaceAll(" ", "");
        List<String> mouses = Arrays.asList(msg.trim().split(","));
        propertiesHandler.addMousesInProperties(mouses);
        sendMessage(chatId, "Мышки добавлены");
    }

    private void addKeyboards(Long chatId, String msg){
        msg = msg.replace("/add_keyboards", "").replaceAll(" ", "");
        List<String> keyboards = Arrays.asList(msg.split(","));
        propertiesHandler.addKeyboardsInProperties(keyboards);
        sendMessage(chatId, "Клавиатуры добавлены");
    }

    private void addHeadphones(Long chatId, String msg){
        msg = msg.replace("/add_headphones", "").replaceAll(" ", "");
        List<String> headphones = Arrays.asList(msg.split(","));
        propertiesHandler.addHeadphonesInProperties(headphones);
        sendMessage(chatId, "Наушники добавлены");
    }

    private void setAlarmDelay(Long chatId, String msg){
        msg = msg.replace("/set_alarm_timeout", "").replaceAll(" ", "");
        propertiesHandler.setAlarmDelay(msg);
        sendMessage(chatId, "Задержка перед тревогой установлена");
    }

    private void setListenDelay(Long chatId, String msg){
        msg = msg.replace("/set_listen_timeout", "").replaceAll(" ", "");
        propertiesHandler.setListenDelay(msg);
        sendMessage(chatId, "Задержка мужду проверками на компьютерах установлена");
    }

    private void startCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в бот, " + userName + "\n" +
                "Твой Chat ID: " + chatId;
        sendMessage(chatId, text);
    }

    private void helpCommand(Long chatId) {
        String text = "Сводка по командам бота\n" +
                "Для добавления переферии используй команды:\n" +
                "/add_mouses и дальше, в этом же сообщении, перечисляй через запятую id мышек\n" +
                "/add_keyboards и дальше, в этом же сообщении, перечисляй через запятую id клавиатур\n" +
                "/add_headphones и дальше, в этом же сообщении, перечисляй через запятую id наушников\n" +
                "\n" +
                "Для изменения таймаутов напиши: \n" +
                "/set_listen_timeout и дальше время в секундах, это периодичность опроса девайсов (лучше ставить значения больше 10 секунд)\n" +
                "/set_alarm_timeout и дальше время в секундах, через сколько после отключения девайса будет отправлено оповещение админам\n" +
                "";
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId){
        String text = "Неизвестная команда";
        sendMessage(chatId, text);
    }

    public void sendAlarmMessage(Long chatId, String pcName, String device){//h_disabled_on_ADM-01
        String text = "⚠Предупреждение⚠\n" +
                "\n" +
                "На компьютере: " + pcName + "\n" +
                "Отключенно утройство: " + device;

        sendMessage(chatId, text, getAlarmKeyboard());
    }
    
    public void sendMessage(Long chatId, String text) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(Long chatId, String text, InlineKeyboardMarkup markup) {
        String chatIdStr = String.valueOf(chatId);
        SendMessage sendMessage = new SendMessage(chatIdStr, text);
        sendMessage.setReplyMarkup(markup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getAlarmKeyboard(){
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Forget for 1h");
        button.setCallbackData("forget");
        rowInline.add(new InlineKeyboardButton());

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}