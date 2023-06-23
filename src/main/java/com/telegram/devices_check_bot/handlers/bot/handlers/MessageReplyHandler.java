package com.telegram.devices_check_bot.handlers.bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class MessageReplyHandler {
    private DevicesCheckBot bot;

    private final PropertiesHandler propertiesHandler = new PropertiesHandler();
    @Value("${admin}")
    private String adminUsername;
    private boolean isInReplyAwait = false;
    private final String AWAIT = "AWAIT";
    private String botPreviousMessageType = AWAIT;


    public MessageReplyHandler(DevicesCheckBot devicesCheckBot) {
        this.bot = devicesCheckBot;
    }

    public void replyToUser(Long chatId, String username, String message) {
        log.info("Message from user: " + message);
        if (isInReplyAwait) {
            if (!botPreviousMessageType.equals(AWAIT)) {
                switch (botPreviousMessageType) {
                    case "mouse" -> addMousesCommand(chatId, message);
                    case "keyboard" -> addKeyboardsCommand(chatId, message);
                    case "headphones" -> addHeadphonesCommand(chatId, message);
                    case "listen" -> setListenDelayCommand(chatId, message);
                    case "alarm" -> setAlarmDelayCommand(chatId, message);
                }
            }
        } else {
            switch (message) {
                case "/start" -> startCommand(chatId, username);
                case "/help" -> helpCommand(chatId);
                case "/add_mouses" -> addMousesCommand(chatId, message);
                case "/add_keyboards" -> addKeyboardsCommand(chatId, message);
                case "/add_headphones" -> addHeadphonesCommand(chatId, message);
                case "/set_listen_timeout" -> setListenDelayCommand(chatId, message);
                case "/set_alarm_timeout" -> setAlarmDelayCommand(chatId, message);
                default -> unknownCommand(chatId);
            }
        }
    }

    private void addMousesCommand(Long chatId, String msg) {

        if (isInReplyAwait) {
            msg = msg.replaceAll(" ", "");
            List<String> mouses = Arrays.asList(msg.trim().split(","));
            propertiesHandler.addMousesInProperties(mouses);
            isInReplyAwait = false;
            bot.sendMessage(chatId, "Мышки добавлены");
        } else {
            bot.sendMessage(chatId, "Ведите Id оборудования");
            botPreviousMessageType = "mouse";
            isInReplyAwait = true;
        }
    }

    private void addKeyboardsCommand(Long chatId, String msg) {
        if (isInReplyAwait) {
            msg = msg.replaceAll(" ", "");
            List<String> keyboards = Arrays.asList(msg.trim().split(","));
            propertiesHandler.addKeyboardsInProperties(keyboards);
            isInReplyAwait = false;
            bot.sendMessage(chatId, "Клавиатуры добавлены");
        } else {
            bot.sendMessage(chatId, "Ведите Id оборудования");
            botPreviousMessageType = "keyboard";
            isInReplyAwait = true;
        }
    }

    private void addHeadphonesCommand(Long chatId, String msg) {
        if (isInReplyAwait) {
            msg = msg.replaceAll(" ", "");
            List<String> headphones = Arrays.asList(msg.trim().split(","));
            propertiesHandler.addHeadphonesInProperties(headphones);
            isInReplyAwait = false;
            bot.sendMessage(chatId, "Наушники добавлены");
        } else {
            bot.sendMessage(chatId, "Ведите Id оборудования");
            botPreviousMessageType = "headphones";
            isInReplyAwait = true;
        }
    }

    private void setAlarmDelayCommand(Long chatId, String msg) {
        if (isInReplyAwait) {
            msg = msg.trim();
            propertiesHandler.setAlarmDelay(msg);
            isInReplyAwait = false;
            bot.sendMessage(chatId, "Задержка перед тревогой установлена");
        } else {
            bot.sendMessage(chatId, "Введите задержку перед тервогой в секундах");
            botPreviousMessageType = "alarm";
            isInReplyAwait = true;
        }
    }

    private void setListenDelayCommand(Long chatId, String msg) {
        if (isInReplyAwait) {
            msg = msg.trim();
            propertiesHandler.setListenDelay(msg);
            isInReplyAwait = false;
            bot.sendMessage(chatId, "Задержка между проверками установлена");
        } else {
            bot.sendMessage(chatId, "Введите задержку между проверками в секундах");
            botPreviousMessageType = "listen";
            isInReplyAwait = true;
        }
    }

    private void startCommand(Long chatId, String userName) {
        String text = "Добро пожаловать в бот, " + userName + "\n" + "Твой Chat ID: " + chatId;
        bot.sendMessage(chatId, text);
    }

    private void helpCommand(Long chatId) {
        String text = """
                Сводка по командам бота
                Для добавления переферии используй команды:
                /add_mouses чтобы добавить мышки
                /add_keyboards чтобы добавить мышки
                /add_headphones чтобы добавить мышки

                Для изменения таймаутов напиши:
                /set_listen_timeout чтобы установить периодичность проверки девайсов
                /set_alarm_timeout чтобы установить задержку перед отправкой сообщения админу
                """;
        bot.sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        String text = "Неизвестная команда";
        bot.sendMessage(chatId, text);
    }

    public void sendAlarmMessage(Long chatId, String pcName, String device) {//h_disabled_on_ADM-01
        String text = "⚠Предупреждение⚠\n" + "\n" + "На компьютере: " + pcName + "\n" + "Отключенно утройство: " + device;

        bot.sendMessage(chatId, text, getAlarmKeyboard(pcName));
    }

    private InlineKeyboardMarkup getAlarmKeyboard(String pcName) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("Forget for 1h");
        button.setCallbackData("forget:" + pcName);
        rowInline.add(button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}

