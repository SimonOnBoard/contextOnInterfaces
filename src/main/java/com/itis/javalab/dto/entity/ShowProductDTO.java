package com.itis.javalab.dto.entity;

import com.itis.javalab.dto.interfaces.Dto;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ShowProductDTO implements Dto {
    private String name;
    private Double price;
    private Integer count;

    public ShowProductDTO(String name, Double price, Integer count) {
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
