package com.itis.javalab.dto.entity;

import com.itis.javalab.dto.interfaces.Dto;

public class AuthDataDTO implements Dto {
    private Long id;
    private String login;
    private String password;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AuthDataDTO(Long id, String login, String password, Long userId) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.userId = userId;
    }

    public AuthDataDTO(String login, String password, Long userId) {
        this.login = login;
        this.password = password;
        this.userId = userId;
    }

    public AuthDataDTO(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
