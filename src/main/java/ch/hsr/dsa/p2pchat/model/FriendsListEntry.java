package ch.hsr.dsa.p2pchat.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class FriendsListEntry {
    private User friend;
    private boolean isOnline;
    private LocalDateTime lastOnlineNotificationTimestamp;

    public FriendsListEntry(User friend) {
        this.friend = friend;
        isOnline = false;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
        lastOnlineNotificationTimestamp = LocalDateTime.now();
    }

    public User getFriend() {
        return friend;
    }

    public LocalDateTime getLastOnlineNotificationTimestamp() {
        return lastOnlineNotificationTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FriendsListEntry that = (FriendsListEntry) o;
        return Objects.equals(friend, that.friend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(friend);
    }
}
