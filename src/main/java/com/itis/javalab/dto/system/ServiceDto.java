package com.itis.javalab.dto.system;

import com.itis.javalab.dto.interfaces.Dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceDto implements Dto {
    private Integer service;
    private Integer chatId;
    private Map<String,Object> resultParams;

    public Object getParametr(String name){
        return this.resultParams.get(name);
    }
}
