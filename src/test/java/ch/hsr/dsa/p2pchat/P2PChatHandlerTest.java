package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatConfiguration;
import ch.hsr.dsa.p2pchat.model.Group;
import ch.hsr.dsa.p2pchat.model.User;
import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class P2PChatHandlerTest {

    private static P2PChatHandler peter;
    private static P2PChatHandler hans;
    private static P2PChatHandler emma;

    @BeforeAll
    public static void setup() throws IOException {
        peter = P2PChatHandler.start( "Peter", 4000);
        hans = P2PChatHandler.start(peter.getPeerAddress(), "Hans", ChatConfiguration.empty(), 4001);
        emma = P2PChatHandler.start(peter.getPeerAddress(), "Emma", ChatConfiguration.empty(), 4002);
    }

    @Test
    public void test_should_receive_chat_message() {
        var testObserver = hans.chatMessages().test();

        peter.sendMessage(new User("Hans"), "Sali Hans");

        testObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Sali Hans"));
    }

    @Test
    public void test_should_receive_friend_request() {
        var testObserver = hans.receivedFriendRequest().test();

        peter.sendFriendRequest(new User("Hans"));

        testObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getFromUser().getName().equals("Peter"));
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

}
