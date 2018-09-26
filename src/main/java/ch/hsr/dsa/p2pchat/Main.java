package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendRequest;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.util.concurrent.ExecutionException;
import io.reactivex.Observable;
import net.tomp2p.utils.Pair;

public class Main {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        var chat = new ChatCLI(new ChatHandler() {
            @Override
            public Observable<ChatMessage> chatMessages() {
                return null;
            }

            @Override
            public Observable<User> friendCameOnline() {
                return null;
            }

            @Override
            public Observable<Pair<User, Group>> userLeftGroup() {
                return null;
            }

            @Override
            public Observable<FriendRequest> receivedFriendRequest() {
                return null;
            }

            @Override
            public void sendMessage(User toUser, String message) {

            }

            @Override
            public void sendFriendRequest(String username) {

            }

            @Override
            public void createGroup(String name) {

            }

            @Override
            public void inviteToGroup(User toUser) {

            }

            @Override
            public void leaveGroup(Group group) {

            }
        });
        var done = chat.start();
        done.get();
    }
}
