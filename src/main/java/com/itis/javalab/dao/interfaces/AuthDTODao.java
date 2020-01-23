package com.itis.javalab.dao.interfaces;

import com.itis.javalab.dto.entity.AuthDataDTO;

import java.util.Optional;


public interface AuthDTODao extends CrudDao<AuthDataDTO> {
    Optional<AuthDataDTO> findByUserId(Long id);
    Optional<AuthDataDTO> findByName(String login);
}
