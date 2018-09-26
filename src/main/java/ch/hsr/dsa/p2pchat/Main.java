package ch.hsr.dsa.p2pchat;

import ch.hsr.dsa.p2pchat.cli.ChatCLI;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String args[]) throws ExecutionException, InterruptedException {
        var chat = new ChatCLI(null);
        var done = chat.start();
        done.get();
    }
}
