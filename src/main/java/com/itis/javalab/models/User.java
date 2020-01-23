package com.itis.javalab.models;

import com.itis.javalab.dto.entity.AuthDataDTO;

public class User {
    private Long id;
    private String userName;
    private String role;
    private AuthDataDTO authDataDTO;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public AuthDataDTO getAuthDataDTO() {
        return authDataDTO;
    }

    public void setAuthDataDTO(AuthDataDTO authDataDTO) {
        this.authDataDTO = authDataDTO;
    }

    public User(String userName, AuthDataDTO authDataDTO) {
        this.userName = userName;
        this.authDataDTO = authDataDTO;
    }

    public User(Long id, String userName, String role, AuthDataDTO authDataDTO) {
        this.id = id;
        this.userName = userName;
        this.role = role;
        this.authDataDTO = authDataDTO;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
