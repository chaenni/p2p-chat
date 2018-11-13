package ch.hsr.dsa.p2pchat.model;

public class AcceptFriendRequestMessage implements Message {
    private User fromUser;

    public AcceptFriendRequestMessage(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    @Override
    public String toString() {
        return "AcceptFriendRequestMessage{" +
            "fromUser=" + fromUser +
            '}';
    }
}
