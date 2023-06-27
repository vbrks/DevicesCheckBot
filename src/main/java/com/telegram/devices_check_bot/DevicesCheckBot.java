package com.telegram.devices_check_bot;

import com.telegram.devices_check_bot.handlers.bot.handlers.CallbackReplyHandler;
import com.telegram.devices_check_bot.handlers.bot.handlers.MessageReplyHandler;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class DevicesCheckBot extends TelegramLongPollingBot {
    @Autowired
    private MessageReplyHandler messageReplyHandler;
    @Autowired
    private CallbackReplyHandler callbackReplyHandler;

    public DevicesCheckBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "DevicesCheckBot";
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        if (!update.hasMessage() && !update.hasCallbackQuery()) {
            return;
        }

        CallbackQuery callback = update.getCallbackQuery();

        if (update.hasMessage()) {
            String username = update.getMessage().getChat().getUserName();
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            messageReplyHandler.replyToUser(chatId, username, message);
        }

        if (update.hasCallbackQuery()) {
            String username = callback.getMessage().getChat().getUserName();
            Long chatId = callback.getMessage().getChatId();
            callbackReplyHandler.replyToUser(chatId, username, callback);
        }
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

    public void sendMessage(String chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, text);

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
}