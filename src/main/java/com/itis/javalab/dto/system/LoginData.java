package com.itis.javalab.dto.system;

import com.itis.javalab.dto.interfaces.Dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
@Data
public class LoginData implements Dto {
    private Map<String,String> loginParams;
}
