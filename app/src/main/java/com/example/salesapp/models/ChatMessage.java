package com.example.salesapp.models;

public class ChatMessage {
    private int chatMessageId;
    private int userId;
    private String senderName;
    private String message;
    private String sentAt;

    public ChatMessage() {
    }

    public ChatMessage(int chatMessageId, int userId, String senderName,
                       String message, String sentAt) {
        this.chatMessageId = chatMessageId;
        this.userId = userId;
        this.senderName = senderName;
        this.message = message;
        this.sentAt = sentAt;
    }

    // Constructor cho gửi tin nhắn mới
    public ChatMessage(int userId, String senderName, String message) {
        this.userId = userId;
        this.senderName = senderName;
        this.message = message;
    }

    // Getters và Setters
    public int getChatMessageId() {
        return chatMessageId;
    }

    public void setChatMessageId(int chatMessageId) {
        this.chatMessageId = chatMessageId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }
}