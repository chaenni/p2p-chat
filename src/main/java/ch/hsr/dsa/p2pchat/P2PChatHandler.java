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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.p2p.builder.BootstrapBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;

public class P2PChatHandler implements ChatHandler {

    private static final String GROUP_PREFIX = "Group: ";
    private final PeerDHT peer;
    private final Observable<ChatMessage> chatMessages;
    private final Observable<GroupMessage> groupChatMessages;
    private final Observable<User> friendCameOnline;
    private final Observable<LeaveMessage> userLeftGroup;
    private final Observable<User> receivedFriendRequest;

    private final ChatConfiguration configuration;

    private final Observable<User> friendRequestAccepted;
    private final Observable<User> friendRequestRejected;

    private final Map<User, FriendsListEntry> friends;

    public static P2PChatHandler start(ChatConfiguration configuration) throws IOException {
        return start(null, configuration);
    }

    public static P2PChatHandler start(InetAddress bootstrapAddress, int port, ChatConfiguration configuration) throws IOException {
        return new P2PChatHandler(peer -> peer.bootstrap().inetAddress(bootstrapAddress).ports(port), configuration);
    }

    public static P2PChatHandler start(PeerAddress bootstrapPeer, ChatConfiguration configuration) throws IOException {
        return new P2PChatHandler(peer -> peer.bootstrap().peerAddress(bootstrapPeer), configuration);
    }


    private P2PChatHandler(Function<Peer, BootstrapBuilder> bootstrapper, ChatConfiguration configuration)
        throws IOException {
        this.configuration = configuration;

        var port = findFreePort();
        this.friends = configuration.getFriends().stream()
            .collect(Collectors.toMap(friend -> friend, FriendsListEntry::new));

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(configuration.getOwnUser().getName()))
            .ports(port).start()).start();
        System.out.println("Chat running on "+peer.peer().peerAddress().inetAddress()+ ":" + port);

        if (bootstrapper != null) {
            try {
                bootstrapper.apply(peer.peer()).start().awaitListeners();
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
        }).share();


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
            .cast(FriendRequest.class)
            .map(FriendRequest::getFromUser)
            .map(user -> {
                configuration.getOpenFriendRequestsToMe().add(user);
                return user;
            });

        groupChatMessages = messageReceived
            .filter(message -> message instanceof GroupMessage)
            .cast(GroupMessage.class);

        friendRequestAccepted = messageReceived
            .filter(message -> message instanceof AcceptFriendRequestMessage)
            .cast(AcceptFriendRequestMessage.class)
            .map(AcceptFriendRequestMessage::getFromUser)
            .filter(configuration.getOpenFriendRequestsFromMe()::contains)
            .map(user -> {
                configuration.getOpenFriendRequestsFromMe().remove(user);
                friends.put(user, new FriendsListEntry(user));
                return user;
            });

        friendRequestRejected = messageReceived
            .filter(message -> message instanceof RejectFriendRequestMessage)
            .cast(RejectFriendRequestMessage.class)
            .map(RejectFriendRequestMessage::getFromUser)
            .filter(configuration.getOpenFriendRequestsFromMe()::contains);

        friendCameOnline.subscribe(friend -> {
            var friendListEntry = this.friends.get(friend);
            if (friendListEntry != null) {
                friendListEntry.setOnline(true);
            }
        });

        friendRequestRejected.subscribe(configuration.getOpenFriendRequestsFromMe()::remove);
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
    public Observable<User> receivedFriendRequest() {
        return receivedFriendRequest;
    }

    @Override
    public Observable<User> friendRequestAccepted() {
        return friendRequestAccepted;
    }

    @Override
    public Observable<User> friendRequestRejected() {
        return friendRequestRejected;
    }

    @Override
    public Collection<FriendsListEntry> friendsList() {
        return friends.values();
    }

    @Override
    public void sendMessage(User toUser, String message) {
        if (isFriendOf(toUser)) {
            sendMessage(toUser, new ChatMessage(configuration.getOwnUser(), message));
        } else {
            // TODO user is not a friend
        }
    }

    @Override
    public void sendGroupMessage(Group group, String message) {
        var groupMessage = new GroupMessage(configuration.getOwnUser(), message, group);
        group.getMembers().stream()
            .filter(member -> !member.equals(configuration.getOwnUser()))
            .forEach(member -> sendMessage(member, groupMessage));
    }

    @Override
    public void sendFriendRequest(User user) {
        if (isFriendOf(user)) {
            // TODO user is already a friend
        } else {
            configuration.getOpenFriendRequestsFromMe().add(user);
            sendMessage(user, new FriendRequest(configuration.getOwnUser()));
        }
    }

    @Override
    public boolean createGroup(String name) {
        Group group = new Group(name, Collections.singletonList(configuration.getOwnUser()));
        Optional<Group> g = getGroup(name);
        if (g.isPresent()) {
            return false;
        }
        try {
            storeGroup(group);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void inviteToGroup(Group group, User toUser) {
        //TODO implement
    }

    @Override
    public boolean leaveGroup(Group group) {
        Optional<Group> g = getGroup(group.getName());

        if (g.isPresent()) {
            List<User> members = new ArrayList<>(g.get().getMembers());
            members.remove(configuration.getOwnUser());
            Group newGroup = new Group(g.get().getName(), members);
            try {
                storeGroup(newGroup);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public Optional<Group> getGroupInformation(String name) {
        return getGroup(name);
    }

    @Override
    public void acceptFriendRequest(User user) {
        if (configuration.getOpenFriendRequestsToMe().contains(user)) {
            friends.put(user, new FriendsListEntry(user));
            sendMessage(user, new AcceptFriendRequestMessage(configuration.getOwnUser()));
            configuration.getOpenFriendRequestsToMe().remove(user);
        }
    }

    @Override
    public void rejectFriendRequest(User user) {
        if (configuration.getOpenFriendRequestsToMe().contains(user)) {
            sendMessage(user, new RejectFriendRequestMessage(configuration.getOwnUser()));
            configuration.getOpenFriendRequestsToMe().remove(user);
        }
    }

    @Override
    public ChatConfiguration getConfiguration() {
        return null;
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
        peer.put(Number160.createHash(configuration.getOwnUser().getName()))
            .object(peer.peer().peerAddress())
            .start()
            .awaitUninterruptibly();
    }

    private void removeOwnAddressFromDHT() {
        peer.remove(Number160.createHash(configuration.getOwnUser().getName()))
            .all()
            .start()
            .awaitUninterruptibly();
    }

    private Optional<Group> getGroup(String name) {
        try {
            return Optional.of((Group) peer.get(Number160.createHash(GROUP_PREFIX + name))
                .start()
                .awaitUninterruptibly()
                .data()
                .object());
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            return Optional.empty();
        }
    }


    private void storeGroup(Group group) throws IOException {
        //TODO check if group already exists
        peer.put(Number160.createHash(GROUP_PREFIX + group.getName())).
            object(group)
            .start()
            .awaitUninterruptibly();
    }

    private boolean isFriendOf(User user) {
        return friendsList().stream().anyMatch(friend -> friend.getFriend().equals(user));
    }

    private static int findFreePort() throws IOException {
        return new ServerSocket(0).getLocalPort();
    }

}
