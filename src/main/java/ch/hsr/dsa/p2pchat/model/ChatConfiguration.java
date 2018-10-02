package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class ChatConfiguration implements Serializable {

    private Set<User> friends;
    private Set<User> openFriendRequestsFromMe;
    private Set<User> openFriendRequestsToMe;

    public ChatConfiguration(Set<User> friends, Set<User> openFriendRequestsFromMe, Set<User> openFriendRequestsToMe) {
        this.friends = friends == null ? new HashSet<>() : friends;
        this.openFriendRequestsFromMe = openFriendRequestsFromMe
            == null ? new HashSet<>() : openFriendRequestsFromMe;
        this.openFriendRequestsToMe = openFriendRequestsFromMe
            == null ? new HashSet<>() : openFriendRequestsFromMe;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<User> getOpenFriendRequestsFromMe() {
        return openFriendRequestsFromMe;
    }

    public void setOpenFriendRequestsFromMe(Set<User> openFriendRequestsFromMe) {
        this.openFriendRequestsFromMe = openFriendRequestsFromMe;
    }

    public Set<User> getOpenFriendRequestsToMe() {
        return openFriendRequestsToMe;
    }

    public void setOpenFriendRequestsToMe(Set<User> openFriendRequestsToMe) {
        this.openFriendRequestsToMe = openFriendRequestsToMe;
    }

    public static ChatConfiguration empty() {
        return new ChatConfiguration(null, null, null);
    }
}
