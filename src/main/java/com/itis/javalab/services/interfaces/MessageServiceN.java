package com.itis.javalab.services.interfaces;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.dto.system.DispatcherDto;
import com.itis.javalab.dto.system.ServiceDto;

import java.time.LocalDateTime;

@Component
public interface MessageServiceN  {
    public ServiceDto getPreparedMessage(DispatcherDto dispatcherDto);
    public String prepareMessage(String message, DecodedJWT jwt);
    public String constructResultLine(String message, DecodedJWT jwt, LocalDateTime now);
    public void saveMessage(String message, LocalDateTime now, DecodedJWT jwt);
}
