package ch.hsr.dsa.p2pchat.model;

public class RejectFriendRequestMessage implements Message {
    private User fromUser;

    public RejectFriendRequestMessage(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }
}
