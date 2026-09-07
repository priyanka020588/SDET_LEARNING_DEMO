package com.company.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;

public final class JsonReader {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonReader() {
    }

    public static JsonNode read(String classpathLocation) {
        try (InputStream stream = JsonReader.class.getClassLoader().getResourceAsStream(classpathLocation)) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing JSON resource: " + classpathLocation);
            }
            return MAPPER.readTree(stream);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + classpathLocation, exception);
        }
    }

    public static <T> T read(String classpathLocation, Class<T> type) {
        try (InputStream stream = JsonReader.class.getClassLoader().getResourceAsStream(classpathLocation)) {
            if (stream == null) {
                throw new IllegalArgumentException("Missing JSON resource: " + classpathLocation);
            }
            return MAPPER.readValue(stream, type);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read " + classpathLocation, exception);
        }
    }
}
