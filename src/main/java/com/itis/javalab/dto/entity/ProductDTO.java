package com.itis.javalab.dto.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ProductDTO {
    private String name;
    private Integer count;
    private Double price;
    private Long dateTime;

    public ProductDTO(String name, Integer count, Double price, Long dateTime) {
        this.name = name;
        this.count = count;
        this.price = price;
        this.dateTime = dateTime;
    }
}
