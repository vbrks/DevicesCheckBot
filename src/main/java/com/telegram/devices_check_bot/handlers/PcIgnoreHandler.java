package com.telegram.devices_check_bot.handlers;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class PcIgnoreHandler {
    private Map<String, LocalDateTime> pcIgnoreList;

    public PcIgnoreHandler(){
        this.pcIgnoreList = new HashMap<>();
    }

    public void addPcToIgnoreList(String pcName) {
        pcIgnoreList.put(pcName, LocalDateTime.now().plusHours(1));
    }
    public void removePcFromIgnoreList(String pcName) {
        pcIgnoreList.remove(pcName);
    }
    public Map<String, LocalDateTime> getPcIgnoreList() {
        return new HashMap<>(this.pcIgnoreList);
    }
}
