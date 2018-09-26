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
        var chat = new ChatCLI(null);
        var done = chat.start();
        done.get();
    }
}
