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
public class DispatcherDto implements Dto {
    private Map<String,Object> params;
    public Object getParametr(String name){
        return this.params.get(name);
    }
}
