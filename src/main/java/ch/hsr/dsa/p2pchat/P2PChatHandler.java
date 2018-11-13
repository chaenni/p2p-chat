package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.AcceptFriendRequestMessage;
import ch.hsr.dsa.p2pchat.model.CertifiedChatMessage;
import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.FriendRequest;
import ch.hsr.dsa.p2pchat.model.FriendsListEntry;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.GroupInvite;
import ch.hsr.dsa.p2pchat.model.GroupMessage;
import ch.hsr.dsa.p2pchat.model.LeaveMessage;
import ch.hsr.dsa.p2pchat.model.Message;
import ch.hsr.dsa.p2pchat.model.MessageState;
import ch.hsr.dsa.p2pchat.model.OnlineNotification;
import ch.hsr.dsa.p2pchat.model.RejectFriendRequestMessage;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.tomp2p.dht.PeerBuilderDHT;
import net.tomp2p.dht.PeerDHT;
import net.tomp2p.futures.BaseFuture;
import net.tomp2p.futures.BaseFutureListener;
import net.tomp2p.p2p.Peer;
import net.tomp2p.p2p.PeerBuilder;
import net.tomp2p.p2p.builder.BootstrapBuilder;
import net.tomp2p.peers.Number160;
import net.tomp2p.peers.PeerAddress;
import net.tomp2p.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class P2PChatHandler implements ChatHandler {

    private static final String GROUP_PREFIX = "Group: ";
    private static final long ONLINE_NOTIFICATION_INTERVAL_S = 10;
    private static final long OFFLINE_TIMEOUT_S = 3 * ONLINE_NOTIFICATION_INTERVAL_S;
    private final Logger logger = LoggerFactory.getLogger(P2PChatHandler.class);

    private final PeerDHT peer;
    private final EthereumAdapter ethereumAdapter;

    private final Observable<ChatMessage> chatMessages;
    private final Observable<GroupMessage> groupChatMessages;
    private final Observable<User> friendCameOnline;
    private final Observable<LeaveMessage> userLeftGroup;
    private final Observable<User> receivedFriendRequest;
    private final Observable<GroupInvite> receivedGroupRequest;

    private final ChatConfiguration configuration;

    private final Observable<User> friendRequestAccepted;
    private final Observable<User> friendRequestRejected;
    private Observable<CertifiedChatMessage> certifiedMessageRequest;

    private final Map<User, FriendsListEntry> friends;
    private final Subject<String> systemMessages;
    private final List<Disposable> disposables = new ArrayList<>();
    private final Observable<User> friendWentOffline;

    public static P2PChatHandler start(ChatConfiguration configuration) throws IOException {
        return start(null, configuration);
    }

    public static P2PChatHandler start(InetAddress bootstrapAddress, int port, ChatConfiguration configuration)
        throws IOException {
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

        systemMessages = PublishSubject.create();

        peer = new PeerBuilderDHT(new PeerBuilder(Number160.createHash(configuration.getOwnUser().getName()))
            .ports(port).start()).start();

        // System out because ui cannot be listening to systemMessages yet
        System.out.println("Chat running on " + peer.peer().peerAddress().inetAddress() + ":" + port);

        if (bootstrapper != null) {
            try {
                bootstrapper.apply(peer.peer()).start().awaitListeners();
            } catch (InterruptedException e) {
                logger.error("Bootstrapping failed.", e);
            }
        }

        storeOwnAddressInDHT();

        this.ethereumAdapter = new EthereumAdapter(configuration.getEthereumWalletPath(),
            configuration.getEthereumWalletPassword());

        var messageReceived = Observable.create(emitter -> {
            peer.peer().objectDataReply((sender, request) -> {
                if (request instanceof Message) {
                    logger.info("Received message: " + request);
                    emitter.onNext(request);
                } else {
                    logger.info("Received message with unhandled type: " + request);
                }
                return null;
            });
        }).share();

        chatMessages = messageReceived
            .filter(message -> message instanceof ChatMessage)
            .cast(ChatMessage.class);

        certifiedMessageRequest = messageReceived
            .filter(message -> message instanceof CertifiedChatMessage)
            .cast(CertifiedChatMessage.class);

        friendCameOnline = messageReceived
            .filter(message -> message instanceof OnlineNotification)
            .cast(OnlineNotification.class)
            .map(OnlineNotification::getUser)
            .filter(friends::containsKey)
            .map(friend -> {
                var friendListEntry = this.friends.get(friend);
                var wasAlreadyOnline = friendListEntry.isOnline();
                friendListEntry.setOnline(true);
                return new Pair<>(!wasAlreadyOnline, friend);
            })
            .filter(Pair::element0)
            .map(Pair::element1);

        userLeftGroup = messageReceived
            .filter(message -> message instanceof LeaveMessage)
            .cast(LeaveMessage.class);

        receivedFriendRequest = messageReceived
            .filter(message -> message instanceof FriendRequest)
            .cast(FriendRequest.class)
            .map(FriendRequest::getFromUser)
            .doOnNext(configuration.getOpenFriendRequestsToMe()::add);

        receivedGroupRequest = messageReceived
            .filter(message -> message instanceof GroupInvite)
            .cast(GroupInvite.class)
            .doOnNext(invite -> {
                configuration.getOpenGroupRequestsToMe().put(invite.getGroupName(), invite);
            });

        groupChatMessages = messageReceived
            .filter(message -> message instanceof GroupMessage)
            .cast(GroupMessage.class);

        friendRequestAccepted = messageReceived
            .filter(message -> message instanceof AcceptFriendRequestMessage)
            .cast(AcceptFriendRequestMessage.class)
            .map(AcceptFriendRequestMessage::getFromUser)
            .filter(configuration.getOpenFriendRequestsFromMe()::contains)
            .doOnNext(user -> {
                configuration.getOpenFriendRequestsFromMe().remove(user);
                friends.put(user, new FriendsListEntry(user));
            });

        friendRequestRejected = messageReceived
            .filter(message -> message instanceof RejectFriendRequestMessage)
            .cast(RejectFriendRequestMessage.class)
            .map(RejectFriendRequestMessage::getFromUser)
            .filter(configuration.getOpenFriendRequestsFromMe()::contains)
            .doOnNext(configuration.getOpenFriendRequestsFromMe()::remove);

        friendWentOffline = Observable
            .interval(OFFLINE_TIMEOUT_S, TimeUnit.SECONDS)
            .flatMap(time -> Observable.fromIterable(friendsList()))
            .filter(FriendsListEntry::isOnline)
            .filter(friend -> !friend.receivedOnlineNotificationsSince(ChronoUnit.SECONDS, OFFLINE_TIMEOUT_S))
            .map(FriendsListEntry::getFriend)
            .doOnNext(friend -> friends.get(friend).setOnline(false));

        disposables.add(Observable
            .interval(ONLINE_NOTIFICATION_INTERVAL_S, TimeUnit.SECONDS)
            .subscribe(time -> {
                var onlineNotification = new OnlineNotification(configuration.getOwnUser());
                friendsList().stream()
                    .map(FriendsListEntry::getFriend)
                    .forEach(friend -> sendMessage(friend, onlineNotification));
            }));
    }

    public void stop() {
        logger.info("Stopping down chat handler.");
        removeOwnAddressFromDHT();
        disposables.forEach(Disposable::dispose);
        peer.shutdown();
        ethereumAdapter.shutdown();
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
    public Observable<User> friendWentOffline() {
        return friendWentOffline;
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
    public Observable<GroupInvite> receivedGroupRequest() {
        return receivedGroupRequest;
    }

    @Override
    public Observable<String> systemMessage() {
        return systemMessages;
    }

    @Override
    public Observable<CertifiedChatMessage> receivedCertifiedMessage() {
        return certifiedMessageRequest;
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
            systemMessages
                .onNext("You can only send messages to friends and " + toUser.getName() + " is not your friend.");
        }
    }

    @Override
    public void sendCertifiedMessage(User toUser, String message) {
        var hash = hashString(
            configuration.getOwnUser().getName() +
                toUser.getName() +
                message +
                System.currentTimeMillis());

        ethereumAdapter.sendMessage(hash).subscribe(i -> {
            var certifiedMessage = new CertifiedChatMessage(configuration.getOwnUser(), message, hash);
            sendMessage(toUser, certifiedMessage);
            systemMessages.onNext("Sent certified message with hash " + certifiedMessage.getBase64Hash());
        });
    }

    @Override
    public void acceptCertifiedMessage(byte[] hash) {
        ethereumAdapter.acceptMessage(hash).subscribe(ignore ->
            systemMessages.onNext("Message accepted successfully."));
    }

    @Override
    public void rejectCertifiedMessage(byte[] hash) {
        ethereumAdapter.rejectMessage(hash).subscribe(ignore ->
            systemMessages.onNext("Message rejected successfully."));
    }

    @Override
    public Observable<MessageState> getCertifiedMessageState(byte[] hash) {
        return ethereumAdapter.getMessageState(hash);
    }

    @Override
    public void sendGroupMessage(Group group, String message) {
        var groupMessage = new GroupMessage(configuration.getOwnUser(), message, group);
        var realGroup = getGroup(group.getName());
        if (realGroup.isPresent()) {
            realGroup.get().getMembers().stream()
                .filter(member -> !member.equals(configuration.getOwnUser()))
                .forEach(member -> sendMessage(member, groupMessage));
        } else {
            systemMessages.onNext("Could not send message to group \"" + group.getName() + "\"");
        }
    }

    @Override
    public void sendFriendRequest(User user) {
        if (isFriendOf(user)) {
            systemMessages.onNext(user.getName() + " is already your friend.");
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
        sendMessage(toUser, new GroupInvite(group.getName()));
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
    public void acceptGroupRequest(Group group) {
        if (configuration.hasGroupInviteToGroup(group)) {
            addUserToGroup(group, configuration.getOwnUser());
        }
    }

    private void addUserToGroup(Group group, User user) {
        Optional<Group> g = getGroup(group.getName());

        if (g.isPresent()) {
            List<User> members = new ArrayList<>(g.get().getMembers());
            members.add(user);
            Group newGroup = new Group(g.get().getName(), members);
            try {
                storeGroup(newGroup);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void rejectGroupRequest(Group group) {
        configuration.getOpenGroupRequestsToMe().remove(group.getName());
    }

    @Override
    public ChatConfiguration getConfiguration() {
        configuration.setFriends(friendsList().stream().map(FriendsListEntry::getFriend).collect(Collectors.toSet()));
        return configuration;
    }

    public PeerAddress getPeerAddress() {
        return peer.peer().peerAddress();
    }

    public Optional<PeerAddress> getPeerAddressForUser(User user) throws IOException, ClassNotFoundException {
        var data = peer.get(Number160.createHash(user.getName()))
            .start()
            .awaitUninterruptibly()
            .data();

        return data == null ? Optional.empty() : Optional.of((PeerAddress) data.object());
    }

    private void sendMessage(User toUser, Message message) {
        Runnable userNotFound = () -> {
            systemMessages.onNext("Peer address for user not found");
            logger.error("User " + toUser.getName() + " not found.");
        };

        try {
            getPeerAddressForUser(toUser)
                .ifPresentOrElse(
                    address -> {
                        peer.peer().sendDirect(address).object(message).start();
                        logger.info("Sent message " + message + " to user " + toUser.getName() + ".");
                    },
                    userNotFound);

        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error while sending message to user " + toUser.getName(), e);
            userNotFound.run();
        }
    }

    private void storeOwnAddressInDHT() throws IOException {
        peer.put(Number160.createHash(configuration.getOwnUser().getName()))
            .object(peer.peer().peerAddress())
            .start()
            .awaitUninterruptibly().addListener(loggerListener("Successfully stored own address in DHT.",
            "Failed to store own address in DHT."));
    }

    private void removeOwnAddressFromDHT() {
        peer.remove(Number160.createHash(configuration.getOwnUser().getName()))
            .all()
            .start()
            .awaitUninterruptibly()
            .addListener(loggerListener("Successfully removed own address from DHT.",
                "Failed to remove own address from DHT."));
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
        peer.put(Number160.createHash(GROUP_PREFIX + group.getName())).
            object(group)
            .start()
            .awaitUninterruptibly();

    }

    private BaseFutureListener<BaseFuture> loggerListener(String messageSuccess, String messageFail) {
        return new BaseFutureListener<>() {
            @Override
            public void operationComplete(BaseFuture future) {
                if (future.isSuccess()) {
                    logger.info(messageSuccess);
                } else {
                    logger.error(messageFail);
                }

            }

            @Override
            public void exceptionCaught(Throwable t) {
                logger.error(messageFail, t);
                System.exit(1);
            }
        };
    }

    private boolean isFriendOf(User user) {
        return friendsList().stream().anyMatch(friend -> friend.getFriend().equals(user));
    }

    private static int findFreePort() throws IOException {
        return new ServerSocket(0).getLocalPort();
    }

    private static byte[] hashString(String string) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(string.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
