package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.model.User;
import java.io.IOException;

public class P2PChatHandlerTest {

    public static void main(String[] args) throws IOException {
        var peter = P2PChatHandler.start( "Peter", 4000);
        var hans = P2PChatHandler.start(peter.getPeerAddress(), "Hans", 4001);

        peter.chatMessages().subscribe(message -> {
            System.out.println("message received");
        });

        hans.chatMessages().subscribe(message -> {
            System.out.println("message received");
        });

        peter.sendMessage(new User("Hans"), "Sali Hans");
        hans.sendMessage(new User("Peter"), "Sali Peter");
    }

}
