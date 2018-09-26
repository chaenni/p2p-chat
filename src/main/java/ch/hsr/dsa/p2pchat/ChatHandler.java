package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendRequest;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;

public interface ChatHandler {
    Observable<ChatMessage> chatMessages();
    Observable<User> friendCameOnline();
    Observable<LeaveMessage> userLeftGroup();
    Observable<FriendRequest> receivedFriendRequest();

    void sendMessage(User toUser, String message);
    void sendGroupMessage(Group group, String message);
    void sendFriendRequest(String username);
    void createGroup(String name);
    void inviteToGroup(Group group, User toUser);
    void leaveGroup(Group group);

}
