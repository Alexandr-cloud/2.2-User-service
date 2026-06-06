package com.aston.event;

public class UserEvent {
    private Long userId;
    private String email;
    private EventType type;

    public UserEvent() {}

    public UserEvent(Long userId, String email, EventType type) {
        this.userId = userId;
        this.email = email;
        this.type = type;
    }

    public enum EventType {
        CREATED, DELETED
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }
}