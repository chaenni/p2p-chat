package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.CertifiedChatMessage;
import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendsListEntry;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.GroupInvite;
import ch.hsr.dsa.p2pchat.model.GroupMessage;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.MessageState;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;
import java.util.Collection;
import java.util.Optional;

public interface ChatHandler {
    Observable<ChatMessage> chatMessages();

    Observable<GroupMessage> groupChatMessages();

    Observable<User> friendCameOnline();
    Observable<User> friendWentOffline();
    Observable<LeaveMessage> userLeftGroup();
    Observable<User> receivedFriendRequest();
    Observable<User> friendRequestAccepted();
    Observable<User> friendRequestRejected();
    Observable<GroupInvite> receivedGroupRequest();
    Observable<String> systemMessage();
    Observable<CertifiedChatMessage> receivedCertifiedMessage();

    Collection<FriendsListEntry> friendsList();

    void sendMessage(User toUser, String message);
    void sendCertifiedMessage(User toUser, String message);
    void acceptCertifiedMessage(byte[] hash);
    void rejectCertifiedMessage(byte[] hash);
    Observable<MessageState> getCertifiedMessageState(byte[] hash);

    void sendGroupMessage(Group group, String message);
    void sendFriendRequest(User user);
    boolean createGroup(String name);
    void inviteToGroup(Group group, User toUser);
    boolean leaveGroup(Group group);
    Optional<Group> getGroupInformation(String name);

    void acceptFriendRequest(User user);
    void rejectFriendRequest(User user);

    void acceptGroupRequest(Group group);
    void rejectGroupRequest(Group group);

    ChatConfiguration getConfiguration();
}
