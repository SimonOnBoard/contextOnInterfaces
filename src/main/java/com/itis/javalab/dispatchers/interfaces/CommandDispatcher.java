package com.itis.javalab.dispatchers.interfaces;

import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.dto.interfaces.Dto;
import com.itis.javalab.servers.Request;


@Component
public interface CommandDispatcher {
    public Dto getCurrentServe(Request request);
}
