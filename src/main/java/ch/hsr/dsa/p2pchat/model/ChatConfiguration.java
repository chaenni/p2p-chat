package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class ChatConfiguration implements Serializable {

    private Set<User> friends;
    private Set<User> openFriendRequestsFromMe;
    private Set<User> openFriendRequestsToMe;

    public ChatConfiguration(Set<User> friends, Set<User> openFriendRequestsFromMe, Set<User> openFriendRequestsToMe) {
        this.friends = friends == null ? Collections.emptySet() : friends;
        this.openFriendRequestsFromMe = openFriendRequestsFromMe
            == null ? Collections.emptySet() : openFriendRequestsFromMe;
        this.openFriendRequestsToMe = openFriendRequestsFromMe
            == null ? Collections.emptySet() : openFriendRequestsFromMe;
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
