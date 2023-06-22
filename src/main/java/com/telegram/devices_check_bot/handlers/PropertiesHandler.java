package com.telegram.devices_check_bot.handlers;

import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class PropertiesHandler {

    private static final String PROP_PATH = "src/main/resources/config.properties";
    private static final String ERR = "ERROR: File not found!";

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


    public String getPropertyByKey(String key) {

        Properties properties = new Properties();
        String value = "";
        try (FileInputStream fis = new FileInputStream(PROP_PATH)) {
            properties.load(fis);
            value = properties.getProperty(key);
        } catch (IOException e) {
            System.err.println(ERR);
        }
        return value;
    }

    public String getAllProperties() {
        try (FileInputStream fis = new FileInputStream(PROP_PATH);
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("e");
        }
    }

    private void addProperty(String key, String property) {

        Properties properties = new Properties();

        try (InputStream fis = new FileInputStream(PROP_PATH);
             OutputStream out = new FileOutputStream(PROP_PATH)) {

            properties.load(fis);


            properties.setProperty(key, property);
            properties.store(out, "");
        } catch (IOException e) {
            System.err.println(ERR);
        }
    }

    private void replaceProperty(String key, String property) {
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(PROP_PATH);
             OutputStream out = new FileOutputStream(PROP_PATH)) {
            properties.load(fis);
            properties.replace(key, property);
            properties.store(out, "");
        } catch (IOException e) {
            System.err.println(ERR);
        }
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
        addProperty("mouses", newProperty);
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
        addProperty("keyboards", newProperty);
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
        addProperty("headphones", newProperty);
    }

    public void setAlarmDelay(String delay) {
        if (Integer.parseInt(delay) > 0) {
            delay = String.valueOf(Integer.parseInt(delay) * 1000);
            replaceProperty("delay.alarm", delay);
        }
    }

    public void setListenDelay(String delay) {
        if (Integer.parseInt(delay) > 0) {
            delay = String.valueOf(Integer.parseInt(delay) * 1000);
            replaceProperty("delay.listen", delay);
        }
    }
}
