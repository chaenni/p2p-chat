package ch.hsr.dsa.p2pchat.model;

public class ChatMessage implements Message {

    private User fromUser;
    private String message;

    public ChatMessage(User fromUser, String message) {
        this.fromUser = fromUser;
        this.message = message;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getMessage() {
        return message;
    }
}
