package com.telegram.devices_check_bot;

import com.telegram.devices_check_bot.handlers.PropertiesHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DevicesCheckBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(DevicesCheckBotApplication.class, args);
    }

}
