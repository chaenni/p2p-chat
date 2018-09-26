package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendRequest;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.Message;
import ch.hsr.dsa.p2pchat.model.OnlineNotification;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;
import java.io.IOException;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class P2PChatHandler implements ChatHandler {

    private PeerDHT peer;
    private Observable<Message> messageReceived;
    private Observable<ChatMessage> chatMessages;
    private Observable<User> friendCameOnline;
    private Observable<LeaveMessage> userLeftGroup;
    private Observable<FriendRequest> receivedFriendRequest;
    private User ownUser;

    public static void main(String[] args) throws Exception {
        var peter = P2PChatHandler.start( "Peter", 4000);
        var hans = P2PChatHandler.start(peter.getPeerAddress(), "Hans", 4001);

        peter.chatMessages.subscribe(message -> {
            System.out.println("Peter received message from " + message.getFromUser().getName() + ": " + message.getMessage());
        });

        hans.chatMessages.subscribe(message -> {
            System.out.println("Hans received message from " + message.getFromUser().getName() + ": " + message.getMessage());
        });

        //peter.sendMessage(new User("Hans"), "Sali Hans");
        //hans.sendMessage(new User("Peter"), "Sali Peter");

        peter.sendMessage(hans.getPeerAddress(), "Sali Hans");
        System.out.println(hans.getPeerAddress());

        hans.sendMessage(peter.getPeerAddress(), "Sali Peter");
    }

    public static P2PChatHandler start( String username, int port) throws IOException {
        return start(null, username, port);
    }

    public static P2PChatHandler start(PeerAddress bootstrapPeer, String username, int port)
        throws IOException {
        return new P2PChatHandler(bootstrapPeer, username, port);
    }

    private P2PChatHandler(PeerAddress bootstrapPeer, String username, int port) throws IOException {
        ownUser = new User(username);

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(username)).ports(port).start()).start();

        if (bootstrapPeer != null){
            try {
                peer.peer().bootstrap().peerAddress(bootstrapPeer).start().awaitListeners();
            } catch (InterruptedException e) {
                System.err.println("Bootstrapping failed.");
            }
        }

        messageReceived = Observable.create(emitter -> {
            peer.peer().objectDataReply((sender, request) -> {
                if (request instanceof Message) emitter.onNext((Message) request);
                return null;
            });
        });

        chatMessages = messageReceived
            .filter(message -> message instanceof ChatMessage)
            .cast(ChatMessage.class);

        friendCameOnline = messageReceived
            .filter(message -> message instanceof OnlineNotification)
            .cast(OnlineNotification.class)
            .map(OnlineNotification::getUser);

        userLeftGroup = messageReceived
            .filter(message -> message instanceof LeaveMessage)
            .cast(LeaveMessage.class);

        receivedFriendRequest = messageReceived
            .filter(message -> message instanceof FriendRequest)
            .cast(FriendRequest.class);
    }

    @Override
    public Observable<ChatMessage> chatMessages() {
        return chatMessages;
    }

    @Override
    public Observable<User> friendCameOnline() {
        return friendCameOnline;
    }

    @Override
    public Observable<LeaveMessage> userLeftGroup() {
        return userLeftGroup;
    }

    @Override
    public Observable<FriendRequest> receivedFriendRequest() {
        return receivedFriendRequest;
    }

    @Override
    public void sendMessage(User toUser, String message) {
        var chatMessage = new ChatMessage(ownUser, message);
        peer.send(Number160.createHash(toUser.getName())).object(chatMessage).start();
    }

    @Override
    public void sendGroupMessage(Group group, String message) {
        //TODO implement
    }

    public void sendMessage(PeerAddress peerAddress, String message) {
        var chatMessage = new ChatMessage(ownUser, message);
        peer.peer().sendDirect(peerAddress).object(chatMessage).start();
    }


    @Override
    public void sendFriendRequest(String username) {
        var friendRequestMessage = new FriendRequest(ownUser);
        peer.send(Number160.createHash(username)).object(friendRequestMessage).start();
    }

    @Override
    public void createGroup(String name) {
        //TODO implement
    }

    @Override
    public void inviteToGroup(Group group, User toUser) {
        //TODO implement
    }

    @Override
    public void leaveGroup(Group group) {
        //TODO implement
    }

    public PeerAddress getPeerAddress() {
        return peer.peer().peerAddress();
    }
}
