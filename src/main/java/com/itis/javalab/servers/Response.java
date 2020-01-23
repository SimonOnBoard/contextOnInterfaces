package com.itis.javalab.servers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class Response {

    public Integer chatId = null;
    public Map<String,String> header = null;
    public Map<String,Object> payload = null;
    public Response() {
        this.chatId = null;
        this.header = new HashMap<>();
        this.payload = new HashMap<>();
    }

    public String getJsonToSend(ObjectMapper objectMapper) {
        Map<String, Object> message = new HashMap<>();
        message.put("header", this.header);
        message.put("payload", this.payload);
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
