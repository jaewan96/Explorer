package com.explorer.realtime.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j
public class MessageConverter {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static JSONObject convert(Object o) {
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String result = objectMapper.writeValueAsString(o);
            log.info("object mapper result : {}", result);
            return new JSONObject(result);
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
