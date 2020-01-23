package com.itis.javalab.services.interfaces;

import com.itis.javalab.context.annotations.Component;
import com.itis.javalab.dto.system.LoginData;
import com.itis.javalab.dto.system.ServiceDto;

@Component
public interface LoginServiceN {
    public ServiceDto startLoginProcess(LoginData data);
    public String checkLogin(LoginData data);
}
