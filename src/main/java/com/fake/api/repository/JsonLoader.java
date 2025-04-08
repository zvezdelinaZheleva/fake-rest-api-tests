package com.fake.api.repository;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class JsonLoader {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <T> List<T> loadObjects(String resourceName, Class<T> clazz) {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
        try {
            return objectMapper.readValue(
                    is, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
