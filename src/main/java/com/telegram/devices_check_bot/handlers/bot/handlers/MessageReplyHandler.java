package com.telegram.devices_check_bot.handlers.bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import com.telegram.devices_check_bot.handlers.PropertiesHandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class MessageReplyHandler {
    private DevicesCheckBot bot;
    @Autowired
    private PropertiesHandler propertiesHandler;
    private String CANCEL_MESSAGE = "Напиши /cancel чтобы отменить текущее действие.";
    private String WRONG_FORMAT_MESSAGE = "Неправильный формат данных, введи число.";
    private final String AWAIT = "AWAIT";
    private String botPreviousMessageType = AWAIT;

    @Autowired
    public MessageReplyHandler(@Lazy DevicesCheckBot devicesCheckBot) {
        this.bot = devicesCheckBot;
    }

    public void replyToUser(Long chatId, String username, String message) {
        log.info("Message from user: " + message);
        if (message.equals("/cancel")) {
            botPreviousMessageType = AWAIT;
        }
        if (chatId == Long.parseLong(propertiesHandler.getAdminChatId())) {
            if (!botPreviousMessageType.equals(AWAIT)) {
                switch (botPreviousMessageType) {
                    case "mouse" -> addMouseCommand(chatId, message);
                    case "keyboard" -> addKeyboardCommand(chatId, message);
                    case "headphones" -> addHeadphonesCommand(chatId, message);
                    case "listen" -> setListenDelayCommand(chatId, message);
                    case "alarm" -> setAlarmDelayCommand(chatId, message);
                    case "delete_mouse" -> deleteMouseCommand(chatId, message);
                    case "delete_keyboard" -> deleteKeyboardCommand(chatId, message);
                    case "delete_headphones" -> deleteHeadphonesCommand(chatId, message);
                    default -> botPreviousMessageType = AWAIT;
                }
            } else {
                switch (message) {
                    case "/start" -> startCommand(chatId, username);
                    case "/help" -> adminHelpCommand(chatId);
                    case "/add_mouses" -> addMouseCommand(chatId, message);
                    case "/add_keyboards" -> addKeyboardCommand(chatId, message);
                    case "/add_headphones" -> addHeadphonesCommand(chatId, message);
                    case "/delete_mouse" -> deleteMouseCommand(chatId, message);
                    case "/delete_keyboard" -> deleteKeyboardCommand(chatId, message);
                    case "/delete_headphone" -> deleteHeadphonesCommand(chatId, message);
                    case "/set_listen_timeout" -> setListenDelayCommand(chatId, message);
                    case "/set_alarm_timeout" -> setAlarmDelayCommand(chatId, message);
                    case "/current_settings" -> currentSettings(chatId);
                    case "/cancel" -> cancelCommand(chatId);
                    default -> unknownCommand(chatId);
                }
            }
        } else {
            switch (message) {
                case "/start" -> startCommand(chatId, username);
                case "/help" -> userHelpCommand(chatId);
                default -> unknownCommand(chatId);
            }
        }
    }

    private void addMouseCommand(Long chatId, String msg) {
        String text = "Ведите Id мышек.\n"
                + "\uD83D\uDCDD Список текущих девайсов: " +
                propertiesHandler.getMousesFromProperties() + "\n" +
                CANCEL_MESSAGE;
        if (!botPreviousMessageType.equals(AWAIT)) {
            propertiesHandler.addMousesInProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Мышка добавлена!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "mouse";
        }
    }

    private void addKeyboardCommand(Long chatId, String msg) {
        String text = "Ведите Id клавиатур.\n"
                + "\uD83D\uDCDD Список текущих девайсов: " +
                propertiesHandler.getKeyboardsFromProperties() + "\n" +
                CANCEL_MESSAGE;
        if (!botPreviousMessageType.equals(AWAIT)) {
            propertiesHandler.addKeyboardsInProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Клавиатура добавлена!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "keyboard";
        }
    }

    private void addHeadphonesCommand(Long chatId, String msg) {
        String text = "Ведите Id наушников.\n" +
                "\uD83D\uDCDD Список текущих девайсов: " +
                propertiesHandler.getHeadphonesFromProperties() + "\n" +
                CANCEL_MESSAGE;
        if (!botPreviousMessageType.equals(AWAIT)) {
            propertiesHandler.addHeadphonesInProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Наушники добавлены!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "headphones";
        }
    }

    private void deleteMouseCommand(Long chatId, String msg) {
        String[] mouses = propertiesHandler.getMousesFromProperties().split(",");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String mouse : mouses) {
            if (mouse.equals("")) continue;
            count++;
            mouse = count + ". " + mouse.trim() + "\n";
            sb.append(mouse);
        }
        String text = "Напиши номер, под которым находится девайс, который ты хочешь удалить.\n" +
                CANCEL_MESSAGE + "\n" + "\n" +
                "\uD83D\uDCDD Список текущих девайсов:\n" + sb;
        if (count == 0) {
            bot.sendMessage(chatId, "Нечего удалять\uD83E\uDD37\u200D♂️");
            return;
        }
        if (!botPreviousMessageType.equals(AWAIT)) {
            if (!StringUtils.isNumeric(msg)) {
                bot.sendMessage(chatId, WRONG_FORMAT_MESSAGE);
                return;
            }
            propertiesHandler.deleteMouseFromProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Мышка удалена!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "delete_mouse";
        }
    }

    private void deleteKeyboardCommand(Long chatId, String msg) {
        String[] keyboards = propertiesHandler.getKeyboardsFromProperties().split(",");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String keyboard : keyboards) {
            if (keyboard.equals("")) continue;
            count++;
            keyboard = count + ". " + keyboard.trim() + "\n";
            sb.append(keyboard);
        }
        String text = "Напиши номер, под которым находится девайс, который ты хочешь удалить.\n" +
                CANCEL_MESSAGE + "\n" + "\n" +
                "\uD83D\uDCDD Список текущих девайсов:\n" + sb;
        if (count == 0) {
            bot.sendMessage(chatId, "Нечего удалять\uD83E\uDD37\u200D♂️");
            return;
        }
        if (!botPreviousMessageType.equals(AWAIT)) {
            if (!StringUtils.isNumeric(msg)) {
                bot.sendMessage(chatId, WRONG_FORMAT_MESSAGE);
                return;
            }
            propertiesHandler.deleteKeyboardFromProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Клавиатура удалена!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "delete_keyboard";
        }
    }

    private void deleteHeadphonesCommand(Long chatId, String msg) {
        String[] headphones = propertiesHandler.getHeadphonesFromProperties().split(",");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (String headphone : headphones) {
            if (headphone.equals("")) continue;
            count++;
            headphone = count + ". " + headphone.trim() + "\n";
            sb.append(headphone);
        }
        String text = "Напиши номер, под которым находится девайс, который ты хочешь удалить.\n" +
                CANCEL_MESSAGE + "\n" + "\n" +
                "\uD83D\uDCDD Список текущих девайсов:\n" + sb;
        if (count == 0) {
            bot.sendMessage(chatId, "Нечего удалять\uD83E\uDD37\u200D♂️");
            return;
        }
        if (!botPreviousMessageType.equals(AWAIT)) {
            if (!StringUtils.isNumeric(msg)) {
                bot.sendMessage(chatId, WRONG_FORMAT_MESSAGE);
                return;
            }
            propertiesHandler.deleteHeadphonesFromProperties(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Наушники удалены!");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "delete_headphones";
        }
    }

    private void setAlarmDelayCommand(Long chatId, String msg) {
        String text = "⏱️ Введите задержку перед тервогой в секундах.\n"
                + "Текущая задержка: " +
                propertiesHandler.getAlarmDelayFromProperties() + " сек.";
        if (!botPreviousMessageType.equals(AWAIT)) {
            msg = msg.trim();
            if (!StringUtils.isNumeric(msg)) {
                bot.sendMessage(chatId, WRONG_FORMAT_MESSAGE);
                return;
            }
            propertiesHandler.setAlarmDelay(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Задержка перед тревогой установлена.");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "alarm";
        }
    }

    private void setListenDelayCommand(Long chatId, String msg) {
        String text = "⏱️ Введите задержку между проверками в секундах.\n"
                + "Текущая задержка: " +
                propertiesHandler.getListenDelayFromProperties() + " сек.";
        if (!botPreviousMessageType.equals(AWAIT)) {
            msg = msg.trim();
            if (!StringUtils.isNumeric(msg)) {
                bot.sendMessage(chatId, WRONG_FORMAT_MESSAGE);
                return;
            }
            propertiesHandler.setListenDelay(msg);
            botPreviousMessageType = AWAIT;
            bot.sendMessage(chatId, "Задержка между проверками установлена.");
        } else {
            bot.sendMessage(chatId, text);
            botPreviousMessageType = "listen";
        }
    }

    private void startCommand(Long chatId, String username) {
        String text = "Добро пожаловать в бот, " + username + "\nНапиши /help для того чтобы увидеть что делают команды бота.";
        propertiesHandler.addUser(username, chatId.toString());
        bot.sendMessage(chatId, text);
    }

    private void adminHelpCommand(Long chatId) {
        String text = """
                📌 Сводка по командам бота 📌
                                
                Для просмотра текущих настроек напиши /get_current_settings
                                
                Для добавления девайсов используй:
                /add_mouses чтобы добавить мышки
                /add_keyboards чтобы добавить клавиатуры
                /add_headphones чтобы добавить наушники
                                
                Для удаления девайсов из списков используй:
                /delete_mouse чтобы удалить мышки
                /delete_keyboard чтобы удалить клавиатуры
                /delete_headphone чтобы удалить наушники
                                
                Для изменения таймаутов напиши:
                /set_listen_timeout чтобы установить периодичность проверки девайсов
                /set_alarm_timeout чтобы установить задержку перед отправкой сообщения админу
                """;
        bot.sendMessage(chatId, text);
    }

    private void userHelpCommand(Long chatId) {
        String text = "У пользователя нету команд";
        bot.sendMessage(chatId, text);
    }

    private void currentSettings(Long chatId) {
        String text = "Задержка между проверками: " + propertiesHandler.getListenDelayFromProperties() + " сек.\n" +
                "Задержка перед тревогой: " + propertiesHandler.getAlarmDelayFromProperties() + " сек.\n" +
                "Мышки: " + propertiesHandler.getMousesFromProperties() + "\n" +
                "Клавиатуры: " + propertiesHandler.getKeyboardsFromProperties() + "\n" +
                "Наушники: " + propertiesHandler.getHeadphonesFromProperties() + "\n";
        bot.sendMessage(chatId, text);
    }

    private void cancelCommand(Long chatId) {
        String text = "Операция отменена";
        bot.sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        String text = "Неизвестная команда";
        bot.sendMessage(chatId, text);
    }

    public void sendAlarmMessage(Long chatId, String pcName, String device) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formatDateTime = now.format(format);
        String text = "⚠Предупреждение⚠\n" +
                "Время: " + formatDateTime +
                "\n" +
                "\nНа компьютере: " + pcName +
                "\nОтключенно утройство: " + device;
        bot.sendMessage(chatId, text, getAlarmKeyboard(pcName));
    }

    private InlineKeyboardMarkup getAlarmKeyboard(String pcName) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDD5B Игнорировать следующий час \uD83D\uDD50");
        button.setCallbackData("forget:" + pcName);
        rowInline.add(button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}

