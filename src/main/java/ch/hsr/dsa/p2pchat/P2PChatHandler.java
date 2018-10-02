package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.AcceptFriendRequestMessage;
import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendRequest;
import ch.hsr.dsa.p2pchat.model.FriendsListEntry;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.GroupMessage;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.Message;
import ch.hsr.dsa.p2pchat.model.OnlineNotification;
import ch.hsr.dsa.p2pchat.model.RejectFriendRequestMessage;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class P2PChatHandler implements ChatHandler {

    private final PeerDHT peer;
    private final Observable<ChatMessage> chatMessages;
    private final Observable<GroupMessage> groupChatMessages;
    private final Observable<User> friendCameOnline;
    private final Observable<LeaveMessage> userLeftGroup;
    private final Observable<FriendRequest> receivedFriendRequest;

    private final User ownUser;
    private final Map<User, FriendsListEntry> friends;
    private final Set<User> openFriendRequestsFromMe;
    private final Set<User> openFriendRequestsToMe;

    public static P2PChatHandler start(String username, int port) throws IOException {
        return start(null, username, ChatConfiguration.empty(), port);
    }

    public static P2PChatHandler start(PeerAddress bootstrapPeer, String username,
        ChatConfiguration configuration, int port) throws IOException {
        return new P2PChatHandler(bootstrapPeer, username, configuration, port);
    }


    private P2PChatHandler(PeerAddress bootstrapPeer, String username, ChatConfiguration configuration, int port)
        throws IOException {
        ownUser = new User(username);

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(username)).ports(port).start()).start();

        this.friends = configuration.getFriends().stream()
            .collect(Collectors.toMap(friend -> friend, FriendsListEntry::new));

        this.openFriendRequestsFromMe = configuration.getOpenFriendRequestsFromMe();
        this.openFriendRequestsToMe = configuration.getOpenFriendRequestsToMe();

        if (bootstrapPeer != null) {
            try {
                peer.peer().bootstrap().peerAddress(bootstrapPeer).start().awaitListeners();
            } catch (InterruptedException e) {
                System.err.println("Bootstrapping failed.");
            }
        }

        storeOwnAddressInDHT();

        var messageReceived = Observable.create(emitter -> {
            peer.peer().objectDataReply((sender, request) -> {
                if (request instanceof Message) {
                    emitter.onNext(request);
                }
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

        groupChatMessages = messageReceived
            .filter(message -> message instanceof GroupMessage)
            .cast(GroupMessage.class);

        friendCameOnline.subscribe(friend -> {
            var friendListEntry = this.friends.get(friend);
            if (friendListEntry != null) {
                friendListEntry.setOnline(true);
            }
        });
    }

    public void stop() {
        removeOwnAddressFromDHT();
        peer.shutdown();
    }

    @Override
    public Observable<ChatMessage> chatMessages() {
        return chatMessages;
    }

    @Override
    public Observable<GroupMessage> groupChatMessages() {
        return groupChatMessages;
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
    public Collection<FriendsListEntry> friendsList() {
        return friends.values();
    }

    @Override
    public void sendMessage(User toUser, String message) {
        sendMessage(toUser, new ChatMessage(ownUser, message));
    }

    @Override
    public void sendGroupMessage(Group group, String message) {
        var groupMessage = new GroupMessage(ownUser, message, group);
        group.getMembers().stream()
            .filter(member -> !member.equals(ownUser))
            .forEach(member -> sendMessage(member, groupMessage));
    }

    @Override
    public void sendFriendRequest(User user) {
        sendMessage(user, new FriendRequest(ownUser));
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

    @Override
    public void acceptFriendRequest(User user) {
        if (openFriendRequestsToMe.contains(user)) {
            friends.put(user, new FriendsListEntry(user));
            sendMessage(user, new AcceptFriendRequestMessage(ownUser));
            openFriendRequestsToMe.remove(user);
        }
    }

    @Override
    public void rejectFriendRequest(User user) {
        if (openFriendRequestsToMe.contains(user)) {
            sendMessage(user, new RejectFriendRequestMessage(ownUser));
            openFriendRequestsToMe.remove(user);
        }
    }

    public PeerAddress getPeerAddress() {
        return peer.peer().peerAddress();
    }

    public PeerAddress getPeerAddressForUser(User user) throws IOException, ClassNotFoundException {
        return (PeerAddress) peer.get(Number160.createHash(user.getName()))
            .start()
            .awaitUninterruptibly()
            .data()
            .object();
    }

    private void sendMessage(User toUser, Message message) {
        try {
            var peerAddress = getPeerAddressForUser(toUser);
            peer.peer().sendDirect(peerAddress).object(message).start();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load peerAddress for user"); //TODO handle error differently
        }
    }

    private void storeOwnAddressInDHT() throws IOException {
        peer.put(Number160.createHash(ownUser.getName()))
            .object(peer.peer().peerAddress())
            .start()
            .awaitUninterruptibly();
    }

    private void removeOwnAddressFromDHT() {
        peer.remove(Number160.createHash(ownUser.getName()))
            .all()
            .start()
            .awaitUninterruptibly();
    }

}
