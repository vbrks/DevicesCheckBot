package com.telegram.devices_check_bot.handlers.bot.handlers;

import com.telegram.devices_check_bot.DevicesCheckBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class CallbackReplyHandler {
    private final DevicesCheckBot bot;
    private Map<String, LocalDateTime> pcIgnoreList;

    public CallbackReplyHandler(DevicesCheckBot devicesCheckBot) {
        this.bot = devicesCheckBot;
        this.pcIgnoreList = new HashMap<>();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::ignoredPcTracker, 0, 5, TimeUnit.SECONDS);
    }

    public void replyToUser(Long chatId, String username, CallbackQuery callback) {
        String[] callbackMessage = callback.getData().split(":");
        String action = callbackMessage[0];
        String pcName = callbackMessage[1];

        switch (action) {
            case "forget" -> addPcToIgnoreList(pcName);
        }
    }

    private void addPcToIgnoreList(String pcName) {
        log.info(pcName + " has been added to ignore list in " + LocalDateTime.now());
        pcIgnoreList.put(pcName, LocalDateTime.now().plusSeconds(5));
    }

    public Map<String, LocalDateTime> getPcIgnoreList() {
        return new HashMap<>(this.pcIgnoreList);
    }

    // TODO: 23.06.2023 переписать foreach, возникает java.util.ConcurrentModificationException
    @Autowired
    private void ignoredPcTracker() {
            if (!pcIgnoreList.isEmpty()) {
                pcIgnoreList.forEach((key, value) -> {
                    if (LocalDateTime.now().isAfter(value)) {
                        pcIgnoreList.remove(key);
                        log.info(key + " УДАЛЕН ИЗ СПИСКА");
                    }
                });
            }
            try {
                Thread.sleep(2000);
                log.info("круг в игноре пройден" + pcIgnoreList.toString());
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
    }
}
