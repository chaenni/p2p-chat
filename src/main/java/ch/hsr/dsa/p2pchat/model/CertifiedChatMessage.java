package ch.hsr.dsa.p2pchat.model;

import java.util.Base64;

public class CertifiedChatMessage implements Message {

    private User fromUser;
    private String message;
    private final String base64Hash;

    public CertifiedChatMessage(User fromUser, String message, byte[] hash) {
        this.fromUser = fromUser;
        this.message = message;
        this.base64Hash = Base64.getEncoder().encodeToString(hash);
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

    public String getBase64Hash() {
        return base64Hash;
    }

    public byte[] getHash() {
        return Base64.getDecoder().decode(base64Hash);
    }
}
