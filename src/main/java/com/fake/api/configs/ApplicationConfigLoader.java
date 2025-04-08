package com.fake.api.configs;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationConfigLoader {


    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ApplicationConfigLoader.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new RuntimeException("application.properties file not found in classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to load configuration: " + ex.getMessage(), ex);
        }
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            throw new RuntimeException("Property " + key + " not found in application.properties");
        }
        return value;
    }
}