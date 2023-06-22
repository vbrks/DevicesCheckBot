package com.telegram.devices_check_bot.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class FilesUtil {

    public static String getFileContent(FileInputStream fis, String encoding ) throws IOException{
        try(BufferedReader br = new BufferedReader( new InputStreamReader(fis, encoding )))
        {
            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}