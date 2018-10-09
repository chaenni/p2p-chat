package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendsListEntry;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.GroupMessage;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public interface ChatHandler {
    Observable<ChatMessage> chatMessages();

    Observable<GroupMessage> groupChatMessages();

    Observable<User> friendCameOnline();
    Observable<LeaveMessage> userLeftGroup();
    Observable<User> receivedFriendRequest();
    Observable<User> friendRequestAccepted();
    Observable<User> friendRequestRejected();

    Collection<FriendsListEntry> friendsList();

    void sendMessage(User toUser, String message);
    void sendGroupMessage(Group group, String message);
    void sendFriendRequest(User user);
    void createGroup(String name) throws IOException;
    void inviteToGroup(Group group, User toUser);
    void leaveGroup(Group group) throws IOException, ClassNotFoundException;
    Optional<Group> getGroupInformation(String name) throws IOException, ClassNotFoundException;

    void acceptFriendRequest(User user);
    void rejectFriendRequest(User user);

}
