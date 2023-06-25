package com.telegram.devices_check_bot.handlers;

import io.netty.handler.codec.socks.SocksRequestType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;

@Component
@Slf4j
public class PropertiesHandler {

    private static final String CONFIG_PROP_PATH = "src/main/resources/config.properties";
    private static final String USERS_PROP_PATH = "src/main/resources/users.properties";
    private static final String ERR = "ERROR: File not found!";
    private Properties properties = new Properties();

    public String getPropertyByKey(String key) {
        String value = "";
        try (FileInputStream fis = new FileInputStream(CONFIG_PROP_PATH)) {
            properties.load(fis);
            value = properties.getProperty(key);
        } catch (IOException e) {
            log.error(ERR);
        }
        return value;
    }

    public String getAllConfigProperties() {
        try (FileInputStream fis = new FileInputStream(CONFIG_PROP_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            log.error(ERR);
        }
        return "Server error has been occurred";
    }

    private void addProperty(String key, String value, String path) {
        try (FileInputStream fis = new FileInputStream(path);) {
            properties.load(fis);
            properties.setProperty(key, value);
        } catch (IOException e) {
            log.error(ERR);
        }

        try (OutputStream out = new FileOutputStream(path)) {
            properties.store(out, null);
        } catch (IOException e) {
            log.error(ERR);
        }
    }

    private void replaceProperty(String key, String value, String path) {
        try (FileInputStream fis = new FileInputStream(path);) {
            properties.load(fis);
            properties.replace(key, value);
        } catch (IOException e) {
            log.error(ERR);
        }

        try (OutputStream out = new FileOutputStream(path)) {
            properties.store(out, null);
        } catch (IOException e) {
            log.error(ERR);
        }
    }

    private void deleteProperty(String key, String path) {
        try (FileInputStream fis = new FileInputStream(path);) {
            properties.load(fis);
            properties.remove(key);
        } catch (IOException e) {
            log.error(ERR);
        }

        try (OutputStream out = new FileOutputStream(path)) {
            properties.store(out, null);
        } catch (IOException e) {
            log.error(ERR);
        }
    }

    public List<String> getUserList() {
        List<String> users = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(USERS_PROP_PATH)) {
            properties.load(fis);
            properties.forEach((key, value) -> users.add(key + "=" + value));
        } catch (IOException e) {
            log.error(ERR);
        }
        return users;
    }

    private boolean isEmpty(String path){
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            return properties.isEmpty();
        } catch (IOException e) {
            log.error(ERR);
        }
        return false;
    }

    public String getMousesFromProperties() {
        return getPropertyByKey("mouses");
    }

    public String getKeyboardsFromProperties() {
        return getPropertyByKey("keyboards");
    }

    public String getHeadphonesFromProperties() {
        return getPropertyByKey("headphones");
    }

    public String getAlarmDelayFromProperties() {
        return getPropertyByKey("delay.alarm");
    }

    public String getListenDelayFromProperties() {
        return getPropertyByKey("delay.listen");
    }

    public void addMousesInProperties(List<String> property) {
        String[] oldMouses = getMousesFromProperties().trim().split(",");
        Set<String> mousesFromProperties = new HashSet<>();

        if (oldMouses.length > 0) {
            mousesFromProperties = new HashSet<>(List.of(oldMouses));
        }
        mousesFromProperties.addAll(property);
        String newProperty = mousesFromProperties.toString()
                .replace("[", "")
                .replace("]", "")
                .trim();
        addProperty("mouses", newProperty, CONFIG_PROP_PATH);
    }

    public void addKeyboardsInProperties(List<String> property) {
        String[] oldKeyboards = getKeyboardsFromProperties().trim().split(",");
        Set<String> keyboardsFromProperties = new HashSet<>();

        if (oldKeyboards.length > 0) {
            keyboardsFromProperties = new HashSet<>(List.of(oldKeyboards));
        }
        keyboardsFromProperties.addAll(property);
        String newProperty = keyboardsFromProperties.toString()
                .replace("[", "")
                .replace("]", "")
                .trim();
        addProperty("keyboards", newProperty, CONFIG_PROP_PATH);
    }

    public void addHeadphonesInProperties(List<String> property) {
        String[] oldHeadphones = getHeadphonesFromProperties().trim().split(",");
        Set<String> headphonesFromProperties = new HashSet<>();

        if (oldHeadphones.length > 0) {
            headphonesFromProperties = new HashSet<>(List.of(oldHeadphones));
        }
        headphonesFromProperties.addAll(property);
        String newProperty = headphonesFromProperties.toString()
                .replace("[", "")
                .replace("]", "")
                .trim();
        addProperty("headphones", newProperty, CONFIG_PROP_PATH);
    }

    public void setAlarmDelay(String delay) {
        if (Integer.parseInt(delay) > 0) {
            delay = String.valueOf(Integer.parseInt(delay) * 1000);
            replaceProperty("delay.alarm", delay, CONFIG_PROP_PATH);
        }
    }

    public void setListenDelay(String delay) {
        if (Integer.parseInt(delay) > 0) {
            delay = String.valueOf(Integer.parseInt(delay) * 1000);
            replaceProperty("delay.listen", delay, CONFIG_PROP_PATH);
        }
    }

    public void addUser(String username, String chatId) {
        System.out.println(isEmpty(USERS_PROP_PATH));
        if (isEmpty(USERS_PROP_PATH)) {
            addProperty(username + ".admin", chatId, USERS_PROP_PATH);
        } else {
            addProperty(username, chatId, USERS_PROP_PATH);
        }
    }

    public void deleteUser(String username) {
        deleteProperty(username, USERS_PROP_PATH);
    }


}
