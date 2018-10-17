package ch.hsr.dsa.p2pchat.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChatConfiguration implements Serializable {

    private User ownUser;
    private Set<User> friends;
    private Set<User> openFriendRequestsFromMe;
    private Set<User> openFriendRequestsToMe;
    private Map<String, GroupInvite> openGroupRequestsToMe; // GroupName

    private ChatConfiguration() {
    }

    public User getOwnUser() {
        return ownUser;
    }

    public Set<User> getFriends() {
        return friends;
    }


    public Set<User> getOpenFriendRequestsFromMe() {
        return openFriendRequestsFromMe;
    }

    public Set<User> getOpenFriendRequestsToMe() {
        return openFriendRequestsToMe;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, GroupInvite> getOpenGroupRequestsToMe() {
        return openGroupRequestsToMe;
    }

    public boolean hasGroupInviteToGroup(Group group) {
        return getOpenGroupRequestsToMe().get(group.getName()) != null;
    }

    public static class Builder {
        private User ownUser;
        private Set<User> friends = new HashSet<>();
        private Set<User> openFriendRequestsFromMe = new HashSet<>();
        private Set<User> openFriendRequestsToMe = new HashSet<>();
        private Map<String, GroupInvite> openGroupRequestsToMe; // GroupName

        private Builder() {}

        public Builder setOwnUser(String username) {
            this.ownUser = new User(username);
            return this;
        }

        public Builder setFriends(Set<User> friends) {
            this.friends = friends;
            return this;
        }

        public Builder setOpenFriendRequestsFromMe(Set<User> openFriendRequestsFromMe) {
            this.openFriendRequestsFromMe = openFriendRequestsFromMe;
            return this;
        }

        public Builder setOpenFriendRequestsToMe(Set<User> openFriendRequestsToMe) {
            this.openFriendRequestsToMe = openFriendRequestsToMe;
            return this;
        }

        public Builder setOpenGroupRequestsToMe(Map<String, GroupInvite> openGroupRequestsToMe) {
            this.openGroupRequestsToMe = openGroupRequestsToMe;
            return this;
        }

        public ChatConfiguration build() {
            ChatConfiguration configuration = new ChatConfiguration();
            configuration.ownUser = ownUser;
            configuration.friends = friends;
            configuration.openFriendRequestsFromMe = openFriendRequestsFromMe;
            configuration.openFriendRequestsToMe = openFriendRequestsFromMe;
            configuration.openGroupRequestsToMe = openGroupRequestsToMe;
            return configuration;
        }
    }
}
