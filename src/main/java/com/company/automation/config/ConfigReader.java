package com.company.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public final class ConfigReader {
    private static final Properties PROPERTIES = load();

    private ConfigReader() {
    }

    private static Properties load() {
        Properties properties = new Properties();
        try (InputStream stream = ConfigReader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (stream == null) {
                throw new IllegalStateException("config.properties was not found on the classpath");
            }
            properties.load(stream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read config.properties", exception);
        }
        return properties;
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isBlank()) {
            return systemValue;
        }
        String envKey = key.replace('.', '_').toUpperCase(Locale.ROOT);
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String fileValue = PROPERTIES.getProperty(key);
        if (fileValue == null) {
            throw new IllegalArgumentException("Missing configuration key: " + key);
        }
        return fileValue;
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
