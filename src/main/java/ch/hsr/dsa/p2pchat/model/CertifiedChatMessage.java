package ch.hsr.dsa.p2pchat.model;

public class CertifiedChatMessage implements Message {

    private User fromUser;
    private String message;
    private final byte[] hash;

    public CertifiedChatMessage(User fromUser, String message, byte[] hash) {
        this.fromUser = fromUser;
        this.message = message;
        this.hash = hash;
    }

    public User getFromUser() {
        return fromUser;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
            "fromUser=" + fromUser +
            ", message='" + message + '\'' +
            '}';
    }

    public byte[] getHash() {
        return hash;
    }
}
