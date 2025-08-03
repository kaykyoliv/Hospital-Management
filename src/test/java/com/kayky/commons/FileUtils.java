package com.kayky.commons;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class FileUtils {

    public static String readResourceFile(String filePath){
        try(InputStream inputStream = FileUtils.class.getResourceAsStream("/" + filePath)){
            if(inputStream == null){
                throw new IllegalArgumentException("Resource not found " + filePath);
            }
            try(Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)){
                if (!sc.hasNext()) {
                    throw new IllegalArgumentException("Resource file is empty: " + filePath);
                }
                return sc.useDelimiter("\\A").next();
            }
        } catch (IOException e){
            throw new RuntimeException("Failed to read resource file: " + filePath, e);
        }
    }
}
