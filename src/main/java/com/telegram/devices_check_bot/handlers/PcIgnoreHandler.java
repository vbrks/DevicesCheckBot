package com.telegram.devices_check_bot.handlers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Component
public class PcIgnoreHandler {
    private Map<String, LocalDateTime> pcIgnoreList;

    public PcIgnoreHandler(){
        this.pcIgnoreList = new HashMap<>();
        startTrackIgnoredPc();
    }

    public void startTrackIgnoredPc(){
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::ignoredPcTracker, 0, 120, TimeUnit.SECONDS);
    }
    public void addPcToIgnoreList(String pcName) {
        pcIgnoreList.put(pcName, LocalDateTime.now().plusHours(1));
    }
    public Map<String, LocalDateTime> getPcIgnoreList() {
        return new HashMap<>(this.pcIgnoreList);
    }

    private void ignoredPcTracker() {
        if (!pcIgnoreList.isEmpty()) {
            Map<String, LocalDateTime> copyOfPcIgnoreList = new HashMap<>(pcIgnoreList);
            copyOfPcIgnoreList.forEach((key, value) -> {
                if (LocalDateTime.now().isAfter(value)) {
                    pcIgnoreList.remove(key);
                }
            });
        }
    }
}
