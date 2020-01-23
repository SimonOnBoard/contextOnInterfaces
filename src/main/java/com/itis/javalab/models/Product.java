package com.itis.javalab.models;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Product {
    private Long id;
    private String name;
    private Double price;
    private Boolean ended;
    private Integer count;

    public Product(Long id, String name, Double price, Boolean ended, Integer count) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.ended = ended;
        this.count = count;
    }
}
