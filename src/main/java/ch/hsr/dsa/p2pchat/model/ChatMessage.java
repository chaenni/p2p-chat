package ch.hsr.dsa.p2pchat.model;

import java.time.LocalDateTime;

public class ChatMessage {

    private String fromUsername;
    private String message;
    private LocalDateTime time;

    public ChatMessage(String fromUsername, String message, LocalDateTime time) {
        this.fromUsername = fromUsername;
        this.message = message;
        this.time = time;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTime() {
        return time;
    }
}
