package ch.hsr.dsa.p2pchat.model;

public class OnlineNotification implements Message  {
    private User user;

    public OnlineNotification(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
