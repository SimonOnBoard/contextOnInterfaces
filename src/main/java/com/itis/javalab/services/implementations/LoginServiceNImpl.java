package com.itis.javalab.services.implementations;

import com.itis.javalab.context.annotations.Autowired;
import com.itis.javalab.dao.interfaces.AuthDTODao;
import com.itis.javalab.dao.interfaces.UserDao;
import com.itis.javalab.dto.entity.AuthDataDTO;
import com.itis.javalab.dto.system.LoginData;
import com.itis.javalab.dto.system.ServiceDto;
import com.itis.javalab.models.User;
import com.itis.javalab.services.interfaces.LoginServiceN;
import com.itis.javalab.services.token.TokenCreator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginServiceNImpl implements LoginServiceN {
    private PasswordEncoder encoder = null;
    @Autowired
    private AuthDTODao authDTODao;
    @Autowired
    private UserDao userDao;

    public LoginServiceNImpl() {
        encoder = new BCryptPasswordEncoder();
    }

    @Override
    public ServiceDto startLoginProcess(LoginData data) {
        String token = this.checkLogin(data);
        return prepareDto(token);
    }

    private ServiceDto prepareDto(String token) {
        Map<String, Object> params = new HashMap<>();
        if (token != null) {
            params.put("status", "200L");
            params.put("token", token);
            params.put("message", "Welcome");
        }
        else{
            params.put("status","failed");
            params.put("message","error");
        }
        return ServiceDto.builder().service(1).resultParams(params).chatId(0).build();

    }

    @Override
    public String checkLogin(LoginData data) {
        Optional<AuthDataDTO> authData = authDTODao.findByName(data.getLoginParams().get("login"));
        if (authData.isPresent()) {
            if (encoder.matches(data.getLoginParams().get("password"), authData.get().getPassword())) {
                Optional<User> user = userDao.findByDTO(authData.get());
                if (user.isPresent()) {
                    String token = TokenCreator.createToken(user.get());
                    return token;
                }
            }
        }
        return null;
    }
}
