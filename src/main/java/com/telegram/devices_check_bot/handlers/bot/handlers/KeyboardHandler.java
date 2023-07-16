package com.telegram.devices_check_bot.handlers.bot.handlers;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardHandler {

    public static InlineKeyboardMarkup getAlarmKeyboard(String pcName) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDD5B Игнорировать до перезагрузки \uD83D\uDD50");
        button.setCallbackData("forget:" + pcName);
        rowInline.add(button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public static InlineKeyboardMarkup getCancelKeyboard(String pcName) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText("\uD83D\uDD5B Отмена \uD83D\uDD50");
        button.setCallbackData("recall:" + pcName);
        rowInline.add(button);

        rowsInline.add(rowInline);
        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }
}
