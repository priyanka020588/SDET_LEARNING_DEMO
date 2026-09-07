package com.company.automation.config;

import java.util.Locale;

public enum Environment {
    LOCAL,
    DEV,
    STAGING,
    PROD;

    public static Environment current() {
        return from(ConfigReader.get("environment"));
    }

    public static Environment from(String value) {
        return Environment.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }

    public boolean isLocal() {
        return this == LOCAL;
    }
}
