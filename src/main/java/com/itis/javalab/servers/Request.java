package com.itis.javalab.servers;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itis.javalab.dto.entity.ProductDTO;
import com.itis.javalab.dto.system.LoginData;
import lombok.Data;

import java.io.IOException;
import java.util.Map;

@Data
public class Request {
    private ObjectMapper objectMapper;
    private static JsonNode root = null;
    private static Map<String,String> header = null;
    private static Map<String,Object> payload = null;
    public static DecodedJWT jwt = null;
    public Request() {
        this.objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        objectMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    public void loadMessage(String inputLine) {
        try {
            this.root = objectMapper.readTree(inputLine);
            this.loadHeader();
            this.loadPayload();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void loadPayload() {
        this.payload = objectMapper.convertValue(root.get("payload"), new TypeReference<Map<String, Object>>(){});
    }

    private void loadHeader() {
        this.header = objectMapper.convertValue(root.get("header"), new TypeReference<Map<String, String>>(){});
    }

    public String getHeaderParam(String parametr){
        return this.header.get(parametr);
    }

    public String getMessage() {
        return (String) payload.get("message");
    }

    public LoginData loadLoginData() {
        try {
            return objectMapper.treeToValue(root.get("payload"), LoginData.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
    public String getPayloadParam(String command) {
        Object ob = payload.get(command);
        if(ob instanceof Number){
            return "" + ob.toString();
        }
        return (String) ob;
    }

    public ProductDTO readProductDto() {
        try {
            return objectMapper.treeToValue(root.get("payload").get("product"), ProductDTO.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException();
        }
    }
}
