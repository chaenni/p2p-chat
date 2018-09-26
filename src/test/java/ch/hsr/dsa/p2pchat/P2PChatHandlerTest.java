package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.ChatMessage;
import ch.hsr.dsa.p2pchat.model.User;
import io.reactivex.observers.TestObserver;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class P2PChatHandlerTest {

    @Test
    public void test_should_receive_message() throws IOException {
        var peter = P2PChatHandler.start( "Peter", 4000);
        var hans = P2PChatHandler.start(peter.getPeerAddress(), "Hans", 4001);

        var testObserver = new TestObserver<ChatMessage>();
        hans.chatMessages().subscribe(testObserver);

        peter.sendMessage(new User("Hans"), "Sali Hans");

        testObserver
            .awaitCount(1)
            .assertValueAt(0, message -> message.getMessage().equals("Sali Hans"));
    }

}
