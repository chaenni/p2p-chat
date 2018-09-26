package ch.hsr.dsa.p2pchat.model;

public class JoinMessage implements Message {
    private User user;

    public JoinMessage(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
