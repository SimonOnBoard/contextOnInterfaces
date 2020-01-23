package com.itis.javalab.services.implementations;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.MessageDao;
import com.itis.javalab.dao.interfaces.UserDao;
import com.itis.javalab.dto.system.DispatcherDto;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.models.Message;
import com.itis.javalab.services.interfaces.MessageServiceN;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class MessageServiceNImpl implements MessageServiceN {
    @Autowired
    private UserDao userDao;
    @Autowired
    private MessageDao messageDao;

    @Override
    public ServiceDto getPreparedMessage(DispatcherDto dispatcherDto) {
        String message = (String) dispatcherDto.getParametr("message");
        DecodedJWT jwt = (DecodedJWT) dispatcherDto.getParametr("jwt");
        message = this.prepareMessage(message, jwt);
        return prepareDto(message);
    }

    private ServiceDto prepareDto(String message) {
        Map<String,Object> params = new HashMap<>();
        params.put("typ",message);
        params.put("message",message);
        return ServiceDto.builder().chatId(1).service(2).resultParams(params).build();
    }

    @Override
    public String prepareMessage(String message, DecodedJWT jwt) {
        LocalDateTime now = LocalDateTime.now();
        saveMessage(message, now, jwt);
        String resultMessage = constructResultLine(message, jwt, now);
        return resultMessage;
    }

    @Override
    public String constructResultLine(String message, DecodedJWT jwt, LocalDateTime now) {
        if (message.equals(".")) {
            return "" + jwt.getClaim("nickName").asString() + " on " + now.toString() + " : " + "Bye";
        }
        return "" + jwt.getClaim("nickName").asString() + " on " + now.toString() + " : " + message;
    }

    @Override
    public void saveMessage(String message, LocalDateTime now, DecodedJWT jwt) {
        messageDao.save(new Message(message, now, jwt.getClaim("id").asLong()));
    }
}
