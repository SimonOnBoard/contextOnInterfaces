package com.itis.javalab.dispatchers;

import com.itis.javalab.context.interfaces.AnotherApplicationContext;
import com.itis.javalab.dispatchers.interfaces.CommandDispatcher;
import com.itis.javalab.dto.interfaces.Dto;
import com.itis.javalab.dto.system.DispatcherDto;
import com.itis.javalab.dto.system.LoginData;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.servers.Request;
import com.itis.javalab.services.interfaces.LoginServiceN;
import com.itis.javalab.services.interfaces.MessageServiceN;
import com.itis.javalab.services.token.TokenVerifyHelper;

import java.util.HashMap;

public class RequestDispatcher {
    private AnotherApplicationContext context;
    private LoginServiceN loginService;
    private MessageServiceN messageService;
    private CommandDispatcher commandDispatcher;
    public RequestDispatcher(AnotherApplicationContext context) {
        this.context = context;
        loginService = context.getComponent("LoginServiceN");
        messageService = context.getComponent("MessageServiceN");
        commandDispatcher = context.getComponent("CommandDispatcher");
    }

    public Dto doDispatch(Request request) {
        switch (request.getHeaderParam("typ")){
            case "login":
                LoginData loginData = request.loadLoginData();
                return loginService.startLoginProcess(loginData);
            case "message":
                checkJWT(request);
                return messageService.getPreparedMessage(this.getMessageDto(request));
            case "logout":
                checkJWT(request);
                return fakeLogoutServiceImpl();
            case "command":
                checkJWT(request);
                return commandDispatcher.getCurrentServe(request);
        }
        return null;
    }

    private DispatcherDto getMessageDto(Request request) {
        HashMap<String,Object> params = new HashMap<>();
        params.put("message",request.getMessage());
        params.put("jwt",request.jwt);
        return DispatcherDto.builder().params(params).build();
    }

    private void checkJWT(Request request) {
        request.jwt = TokenVerifyHelper.verify(request.getHeaderParam("bearer"));
    }

    //ВРЕМЕНИ НЕ ХВАТИЛО, СТОИТ ЗАГЛУШКА
    private ServiceDto fakeLogoutServiceImpl() {
        HashMap<String,Object> params = new HashMap<>();
        params.put("typ","logout");
        params.put("message","Пока:)");
        return ServiceDto.builder().chatId(0).resultParams(params).service(2).build();
    }
}
