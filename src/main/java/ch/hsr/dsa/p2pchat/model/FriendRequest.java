package ch.hsr.dsa.p2pchat.model;

public class FriendRequest implements Message {
    private User fromUser;

    public FriendRequest(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }
}
