package com.itis.javalab.models;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private String text;
    private LocalDateTime dateTime;
    private Long ownerId;
    private Long receiverId;

    public Message(String text, LocalDateTime dateTime, Long ownerId) {
        this.text = text;
        this.dateTime = dateTime;
        this.ownerId = ownerId;
    }

    public Message(String text, LocalDateTime dateTime, Long ownerId, Long receiverId) {
        this.text = text;
        this.dateTime = dateTime;
        this.ownerId = ownerId;
        this.receiverId = receiverId;
    }

    public Message(Long id, String text, LocalDateTime dateTime, Long ownerId, Long receiverId) {
        this.id = id;
        this.text = text;
        this.dateTime = dateTime;
        this.ownerId = ownerId;
        this.receiverId = receiverId;
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

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
