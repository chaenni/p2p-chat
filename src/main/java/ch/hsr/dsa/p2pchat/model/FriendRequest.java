package ch.hsr.dsa.p2pchat.model;

import java.util.Objects;

public class FriendRequest implements Message {
    private User fromUser;

    public FriendRequest(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(fromUser, that.fromUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fromUser);
    }
}
