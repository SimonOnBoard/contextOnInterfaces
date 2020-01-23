package com.itis.javalab.dto.entity;

import com.itis.javalab.dto.interfaces.Dto;

public class MessageDTO implements Dto {
    private Long id;
    private String text;
    private String name;
    private Long dateTime;


    public MessageDTO(Long id, String text, String name, Long dateTime) {
        this.id = id;
        this.text = text;
        this.name = name;
        this.dateTime = dateTime;
    }

    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
