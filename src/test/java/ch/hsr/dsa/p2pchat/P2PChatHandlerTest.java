package ch.hsr.dsa.p2pchat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class P2PChatHandlerTest {

    private P2PChatHandler peter;
    private P2PChatHandler hans;
    private P2PChatHandler emma;

    @BeforeEach
    public void setup() throws IOException {
        peter = P2PChatHandler.start(ChatConfiguration.builder().setOwnUser("Peter").build());

        hans = P2PChatHandler.start(peter.getPeerAddress(), ChatConfiguration.builder().setOwnUser("Hans").build());

        emma = P2PChatHandler.start(peter.getPeerAddress(), ChatConfiguration.builder().setOwnUser("Emma").build());
    }

    @Test
    public void test_should_receive_chat_message_from_friend() {

        var testMessageObserver = hans.chatMessages().test();

        hans.receivedFriendRequest()
            .delay(300, TimeUnit.MILLISECONDS)
            .subscribe(hans::acceptFriendRequest);

        peter.friendRequestAccepted()
            .delay(300, TimeUnit.MILLISECONDS)
            .subscribe(user -> peter.sendMessage(user, "Sali"));

        peter.sendFriendRequest(new User("Hans"));

        testMessageObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Sali"));

        testMessageObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Sali"));
    }

    @Test
    public void test_should_not_receive_chat_message_from_not_friend() {
        var testObserver = hans.chatMessages().test();
        peter.sendMessage(new User("Hans"), "Sali Hans");

        testObserver
            .assertNoValues();
    }

    @Test
    public void test_should_receive_friend_request() {
        var testObserver = hans.receivedFriendRequest().test();

        peter.sendFriendRequest(new User("Hans"));

        testObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getName().equals("Peter"));
    }

    @Test
    public void test_should_receive_friend_request_accept_message_when_accepting_friend_requests() {
        hans.receivedFriendRequest()
            .delay(300, TimeUnit.MILLISECONDS)
            .subscribe(hans::acceptFriendRequest);

        var testObserver = peter.friendRequestAccepted().test();

        peter.sendFriendRequest(new User("Hans"));

        testObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getName().equals("Hans"));
    }


    @Test
    public void test_friend_list_should_be_updated_on_friend_request_accept() {
        hans.receivedFriendRequest()
            .delay(300, TimeUnit.MILLISECONDS)
            .subscribe(hans::acceptFriendRequest);

        var testObserver = peter.friendRequestAccepted().test();

        peter.sendFriendRequest(new User("Hans"));

        testObserver.awaitCount(1).assertOf(userTestObserver -> {
            assertTrue(peter.friendsList().stream().allMatch(friend -> friend.getFriend().getName().equals("Hans")));
            assertTrue(hans.friendsList().stream().allMatch(friend -> friend.getFriend().getName().equals("Peter")));
        });
    }

    @Test
    public void test_should_receive_group_message() {
        var testObserverEmma = emma.groupChatMessages().test();
        var testObserverHans = hans.groupChatMessages().test();

        var group = new Group("name", Arrays.asList(
            new User("Emma"),
            new User("Hans"),
            new User("Peter")));

        peter.sendGroupMessage(group, "Hello");

        testObserverEmma
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Hello"));

        testObserverHans
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Hello"));
    }

    @Test
    public void test_should_not_receive_group_message_from_own_user() {
        var testObserver = peter.groupChatMessages().test();

        var group = new Group("name", Arrays.asList(
            new User("Emma"),
            new User("Hans"),
            new User("Peter")));

        peter.sendGroupMessage(group, "Hello");

        testObserver.assertNoValues();
    }

    @Test
    public void test_should_create_group() throws IOException, ClassNotFoundException {
        String name = "Verein4";
        peter.createGroup(name);
        Optional<Group> group = peter.getGroupInformation(name);
        assumeTrue(group.isPresent());
        Group g = group.get();
        assertEquals(name, g.getName());
    }

    @Test
    public void test_group_created_should_contain_himself() throws IOException, ClassNotFoundException {
        String name = "Verein3";
        peter.createGroup(name);
        Optional<Group> group = peter.getGroupInformation(name);
        assumeTrue(group.isPresent());
        Group g = group.get();
        assertTrue(g.getMembers().size() == 1 && g.getMembers().contains(new User("Peter")));
    }

    @Test
    public void test_group_information_visible_to_non_user() throws IOException, ClassNotFoundException {
        String name = "LegoGroup";
        peter.createGroup(name);
        Optional<Group> group = hans.getGroupInformation(name);
        assumeTrue(group.isPresent());
        Group g = group.get();
        assertTrue(g.getMembers().size() == 1 && g.getMembers().contains(new User("Peter")));
    }


    @Test
    public void test_cannot_Create_group_with_same_name() {
        String name = "Verein2";
        peter.createGroup(name);
        assertFalse(hans.createGroup(name));
    }

    @Test
    public void test_leaving_group_one_member_less() {
        String name = "Verein1";
        peter.createGroup(name);
        peter.leaveGroup(new Group(name, Collections.emptyList()));
        Optional<Group> group = peter.getGroupInformation(name);
        assumeTrue(group.isPresent());
        Group g = group.get();
        assertEquals(0, g.getMembers().size());
    }
}
