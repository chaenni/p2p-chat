package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.User;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class P2PChatHandlerTest {

    @Test
    public void test_sendMessageFromPeerToOtherPeer() throws IOException {
        var peter = P2PChatHandler.start( "Peter", 4000);
        var hans = P2PChatHandler.start(peter.getPeerAddress(), "Hans", 4001);

        peter.chatMessages()
            .test()
            .assertValueAt(0, message -> message.getMessage().equals("Sali Peter"));

        hans.chatMessages()
            .test()
            .assertValueAt(0, message -> message.getMessage().equals("Sali Hans"));

        peter.sendMessage(new User("Hans"), "Sali Hans");
        hans.sendMessage(new User("Peter"), "Sali Peter");
    }

}
