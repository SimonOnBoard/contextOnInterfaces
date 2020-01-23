package com.itis.javalab.dao.interfaces;

import com.itis.javalab.dto.entity.AuthDataDTO;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface UserRowMapper<T> {
    T mapRow(ResultSet row, AuthDataDTO dataDTO) throws SQLException;
}
