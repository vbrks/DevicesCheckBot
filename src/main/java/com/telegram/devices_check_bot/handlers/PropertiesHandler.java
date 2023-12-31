package com.telegram.devices_check_bot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Component
@Slf4j
public class PropertiesHandler {
    private static String path = new File(PropertiesHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
    private static final String CONFIG_PROP_PATH = path + "\\config.properties";
    private static final String USERS_PROP_PATH = path + "\\users.properties";
    private static final String ERR = "ERROR: File not found!";
    private static final String MOUSES = "mouses";
    private static final String KEYBOARDS = "keyboards";
    private static final String HEADPHONES = "headphones";

    @Autowired
    private void createConfigFiles(){
        if (Files.notExists(Path.of(CONFIG_PROP_PATH))){
            try (FileWriter writer = new FileWriter(CONFIG_PROP_PATH, false)) {
                String config = """
                delay.alarm=3000
                delay.listen=4000
                headphones=
                keyboards=
                mouses=""";
                writer.write(config);
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (Files.notExists(Path.of(USERS_PROP_PATH))){
            File usersConfig = new File(USERS_PROP_PATH);
            try {
                usersConfig.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getPropertyByKey(String key, String path) {
        Properties properties = new Properties();
        String value = "";
        try (FileInputStream fis = new FileInputStream(path)) {
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
        Properties properties = new Properties();
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
        Properties properties = new Properties();
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

    public Map<String, String> getUserList() {
        Properties properties = new Properties();
        Map<String, String> users = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(USERS_PROP_PATH)) {
            properties.load(fis);
            properties.forEach((key, value) -> users.put((String) key, (String) value));
        } catch (IOException e) {
            log.error(ERR);
        }
        return users;
    }

    private boolean propertiesIsEmpty(String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            return properties.isEmpty();
        } catch (IOException e) {
            log.error(ERR);
        }
        return false;
    }

    private boolean propertiesContainsValue(String value, String path) {
        Properties properties = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            return properties.contains(value);
        } catch (IOException e) {
            log.error(ERR);
        }
        return false;
    }

    public String getMousesFromProperties() {
        return getPropertyByKey(MOUSES, CONFIG_PROP_PATH);
    }

    public String getKeyboardsFromProperties() {
        return getPropertyByKey(KEYBOARDS, CONFIG_PROP_PATH);
    }

    public String getHeadphonesFromProperties() {
        return getPropertyByKey(HEADPHONES, CONFIG_PROP_PATH);
    }

    public void deleteMouseFromProperties(String deviceNumber) {
        int numToDelete = Integer.parseInt(deviceNumber);
        List<String> mouses = new ArrayList<>(Arrays.stream(getMousesFromProperties().split(", ")).toList());
        mouses.remove(numToDelete - 1);
        replaceProperty(MOUSES, "", CONFIG_PROP_PATH);
        addMousesInProperties(mouses.toString().replace("[", "").replace("]", ""));
    }

    public void deleteKeyboardFromProperties(String deviceNumber) {
        int numToDelete = Integer.parseInt(deviceNumber);
        List<String> keyboards = new ArrayList<>(Arrays.stream(getKeyboardsFromProperties().split(", ")).toList());
        keyboards.remove(numToDelete - 1);
        replaceProperty(KEYBOARDS, "", CONFIG_PROP_PATH);
        addKeyboardsInProperties(keyboards.toString().replace("[", "").replace("]", ""));
    }

    public void deleteHeadphonesFromProperties(String deviceNumber) {
        int numToDelete = Integer.parseInt(deviceNumber);
        List<String> headphones = new ArrayList<>(Arrays.stream(getHeadphonesFromProperties().split(", ")).toList());
        headphones.remove(numToDelete - 1);
        replaceProperty(HEADPHONES, "", CONFIG_PROP_PATH);
        addHeadphonesInProperties(headphones.toString().replace("[", "").replace("]", ""));
    }

    public String getAlarmDelayFromProperties() {
        int timeoutInSeconds = Integer.parseInt(getPropertyByKey("delay.alarm", CONFIG_PROP_PATH)) / 1000;
        return String.valueOf(timeoutInSeconds);
    }

    public String getListenDelayFromProperties() {
        int timeoutInSeconds = Integer.parseInt(getPropertyByKey("delay.listen", CONFIG_PROP_PATH)) / 1000;
        return String.valueOf(timeoutInSeconds);
    }

    public void addMousesInProperties(String property) {
        String mousesFromProperties = getMousesFromProperties().trim();
        if (!mousesFromProperties.equals("")) {
            if (!mousesFromProperties.contains(property)) {
                mousesFromProperties += ", " + property;
            } else return;
        } else {
            addProperty(MOUSES, property, CONFIG_PROP_PATH);
            return;
        }
        addProperty(MOUSES, mousesFromProperties, CONFIG_PROP_PATH);
    }

    public void addKeyboardsInProperties(String property) {
        String keyboardsFromProperties = getKeyboardsFromProperties().trim();
        if (!keyboardsFromProperties.equals("")) {
            if (!keyboardsFromProperties.contains(property)) {
                keyboardsFromProperties += ", " + property;
            } else return;
        } else {
            addProperty(KEYBOARDS, property, CONFIG_PROP_PATH);
            return;
        }
        addProperty(KEYBOARDS, keyboardsFromProperties, CONFIG_PROP_PATH);
    }

    public void addHeadphonesInProperties(String property) {
        String headphonesFromProperties = getHeadphonesFromProperties().trim();
        if (!headphonesFromProperties.equals("")) {
            if (!headphonesFromProperties.contains(property)) {
                headphonesFromProperties += ", " + property;
            } else return;
        } else {
            replaceProperty(HEADPHONES, property, CONFIG_PROP_PATH);
            return;
        }
        replaceProperty(HEADPHONES, headphonesFromProperties, CONFIG_PROP_PATH);
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
        if (propertiesIsEmpty(USERS_PROP_PATH)) {
            addProperty("admin." + username, chatId, USERS_PROP_PATH);
        } else if (!propertiesContainsValue(chatId, USERS_PROP_PATH)) {
            addProperty(username, chatId, USERS_PROP_PATH);
        }
    }

    public String getAdminChatId() {
        Map<String, String> users = getUserList();
        StringBuilder sb = new StringBuilder();
        users.forEach((username, chatId) -> {
            if (username.contains("admin")) sb.append(chatId);
        });
        if (!sb.toString().equals("")) {
            return sb.toString();
        }
        return "0";
    }

    public String getAdminUsername() {
        Map<String, String> users = getUserList();
        StringBuilder sb = new StringBuilder();
        users.forEach((username, chatId) -> {
            if (username.contains("admin")) sb.append(username);
        });
        return sb.toString().replace("admin.", "");
    }
}
